package com.buildit;

import com.buildit.invoicing.domain.models.Invoice;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.http.Http;
import org.springframework.integration.dsl.mail.Mail;
import org.springframework.integration.dsl.support.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.io.IOUtils;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class ProcurementApplication {
    @Configuration
    static class ObjectMapperCustomizer {
        @Autowired
        @Qualifier("_halObjectMapper")
        private ObjectMapper springHateoasObjectMapper;

        @Bean(name = "objectMapper")
        ObjectMapper objectMapper() {
            return springHateoasObjectMapper
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                    .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    .registerModules(new JavaTimeModule());
        }

        @Bean
        public RestTemplate restTemplate() {
            RestTemplate _restTemplate = new RestTemplate();
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            messageConverters.add(new MappingJackson2HttpMessageConverter(springHateoasObjectMapper));
            _restTemplate.setMessageConverters(messageConverters);
            return _restTemplate;
        }
    }

    @Bean
    IntegrationFlow inboundHttpGateway() {
        return IntegrationFlows.from(
                Http.inboundChannelAdapter("/api/inventory/invoices").requestPayloadType(String.class))
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
    @Bean
    IntegrationFlow inboundMail() {
        return IntegrationFlows.from(Mail.imapInboundAdapter(
                String.format("imaps://%s:%s@imap.gmail.com:993/INBOX", gmailUsername, gmailPassword)
                )
                        .selectorExpression("subject matches '.*invoice.*'")
                ,e->e.autoStartup(true).poller(Pollers.fixedDelay(10000)))
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
                .handle(i -> System.out.println(((Invoice)i.getPayload()).getAmount()))
                .get();
    }

    @Bean
    IntegrationFlow fastTrack() {
        return IntegrationFlows.from("fasttrack-channel")
                .transform(Transformers.fromJson(Invoice.class))
                .handle(System.err::println)
                .get();
    }

    public static void main(String[] args) {
        SpringApplication.run(ProcurementApplication.class, args);
    }
}
