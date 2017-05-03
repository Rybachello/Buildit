package com.buildit.invoicing.application;

import com.buildit.invoicing.domain.model.Invoice;
import com.buildit.invoicing.domain.repository.InvoiceRepository;
import com.buildit.invoicing.service.InvoicingService;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repository.PlantHireRequestRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.http.Http;
import org.springframework.integration.dsl.mail.Mail;
import org.springframework.integration.dsl.support.Transformers;
import org.springframework.stereotype.Service;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

/**
 * Created by Vasiliy on 2017-05-03.
 */
@Configuration
public class InboundProcessor {

    @Autowired
    InvoicingService invoicingService;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    PlantHireRequestRepository phrRepository;

    @Bean
    IntegrationFlow inboundHttpGateway() {
        return IntegrationFlows.from(
                Http.inboundChannelAdapter("/api/inventory/invoices")
                        .crossOrigin(spec->spec.origin("*"))
                        .requestPayloadType(String.class))
                .channel("router-channel")
                .get();
    }

    @Service
    class InvoiceProcessor {
        public String extractInvoice(MimeMessage msg) throws Exception {
            Multipart multipart = (Multipart) msg.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.getContentType().contains("json") &&
                        bodyPart.getFileName().startsWith("invoice"))
                    return IOUtils.toString(bodyPart.getInputStream(), "UTF-8");
            }
            throw new Exception("oops at extractInvoice");
        }
    }

    @Value("${gmail.username}")
    String gmailUsername;

    @Value("${gmail.password}")
    String gmailPassword;

    final int mailRefreshPeriodSeconds = 60;

    @Bean
    IntegrationFlow inboundMail() {
        return IntegrationFlows.from(Mail.imapInboundAdapter(
                String.format("imaps://%s:%s@imap.gmail.com:993/INBOX", gmailUsername, gmailPassword)
                )
                        .selectorExpression("subject matches '.*invoice.*'")
                , e -> e.autoStartup(true).poller(Pollers.fixedDelay(mailRefreshPeriodSeconds * 1000)))
                .transform("@invoiceProcessor.extractInvoice(payload)")
                .channel("router-channel")
                .get();
    }

    @Bean
    IntegrationFlow router() {
        return IntegrationFlows.from("router-channel")
                .route("#jsonPath(payload, '$.amount') > 1000", routes -> routes
                        .subFlowMapping("false", subflow -> subflow.channel("fasttrack-channel"))
                        .subFlowMapping("true", subflow -> subflow.channel("normaltrack-channel"))
                )
                .get();
    }

    @Bean
    IntegrationFlow normalTrack() {
        return IntegrationFlows.from("normaltrack-channel")
                .transform(Transformers.fromJson(Invoice.class))
                .handle(i -> normalTrackHandler(((Invoice) i.getPayload())))
                .get();
    }

    @Bean
    IntegrationFlow fastTrack() {
        return IntegrationFlows.from("fasttrack-channel")
                .transform(Transformers.fromJson(Invoice.class))
                .handle(i -> fastTrackHandler(((Invoice) i.getPayload())))
                .get();
    }

    void normalTrackHandler(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    void fastTrackHandler(Invoice invoice) {
        invoiceRepository.save(invoice);
        if (invoice.getPurchaseOrder() == null)
            return;

        PlantHireRequest po = phrRepository.findByPOID(invoice.getPurchaseOrder().getPurchaseOrderId());
        if (po == null)
            return;

        if (po!=null&&po.getCost()==invoice.getAmount()){
            invoicingService.sendRemittanceAdvice(po.getId(), invoice.getAmount());
        }
    }
}
