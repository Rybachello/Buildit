package com.buildit.invoicing.application;

import com.buildit.invoicing.application.dto.InvoiceDTOWithoutLocalDateVariableWhichForSomeReasonCanNotBeDeserialized;
import com.buildit.invoicing.domain.model.Invoice;
import com.buildit.invoicing.domain.repository.InvoiceRepository;
import com.buildit.invoicing.services.InvoicingService;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repository.PlantHireRequestRepository;
import com.buildit.rental.domain.model.PurchaseOrder;
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

@Service
class InvoiceProcessor {
    public String extractInvoice(MimeMessage msg) throws Exception {
        System.out.println("processing message started");
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
                        .selectorExpression("subject matches '.*Invoice.*'")
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
                .transform(Transformers.fromJson(InvoiceDTOWithoutLocalDateVariableWhichForSomeReasonCanNotBeDeserialized.class))
                .handle(i -> normalTrackHandler((InvoiceDTOWithoutLocalDateVariableWhichForSomeReasonCanNotBeDeserialized) i.getPayload()))
                .get();
    }

    @Bean
    IntegrationFlow fastTrack() {
        return IntegrationFlows.from("fasttrack-channel")
                .transform(Transformers.fromJson(InvoiceDTOWithoutLocalDateVariableWhichForSomeReasonCanNotBeDeserialized.class))
                .handle(i -> fastTrackHandler(((InvoiceDTOWithoutLocalDateVariableWhichForSomeReasonCanNotBeDeserialized) i.getPayload())))
                .get();
    }

    void normalTrackHandler(InvoiceDTOWithoutLocalDateVariableWhichForSomeReasonCanNotBeDeserialized dto) {
        Invoice invoice = new Invoice(dto.get_id(), false, null, null, null, dto.getAmount(), PurchaseOrder.of(dto.getOrderId(), null));

        invoiceRepository.save(invoice);
    }

    void fastTrackHandler(InvoiceDTOWithoutLocalDateVariableWhichForSomeReasonCanNotBeDeserialized dto) {
        Invoice invoice = new Invoice(dto.get_id(), false, null, null, null, dto.getAmount(), PurchaseOrder.of(dto.getOrderId(), null));
        invoiceRepository.save(invoice);

        PlantHireRequest po = phrRepository.findByPOID(invoice.getPurchaseOrder().getPurchaseOrderId());
        if (po == null)
            return;

        if (po!=null&&po.getCost()==invoice.getAmount()){
            invoice.approve();
            invoicingService.sendRemittanceAdvice(po.getId(), invoice.getId());
        }
    }
}
