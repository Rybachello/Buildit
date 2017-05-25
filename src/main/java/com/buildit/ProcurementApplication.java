package com.buildit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@MessagingGateway
interface OutboundGateway {
    @Gateway(requestChannel = "requestChannel")
    @Payload("new java.util.Date()")
    String queryResource1();
}

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
        public ClientHttpRequestFactory requestFactory() {
            return new BasicSecureSimpleClientHttpRequestFactory();
        }

        @Bean
        public RestTemplate restTemplate() {
            RestTemplate _restTemplate = new RestTemplate(requestFactory());
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            messageConverters.add(new MappingJackson2HttpMessageConverter(springHateoasObjectMapper));
            _restTemplate.setMessageConverters(messageConverters);
            return _restTemplate;
        }

        @Configuration
        @PropertySource("classpath:credentials.properties")
        @ConfigurationProperties
        @Data
        class CredentialsProperties {
            private Map<String, Map<String, String>> credentials;
        }

        static class BasicSecureSimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
            @Autowired
            CredentialsProperties credentials;

            public BasicSecureSimpleClientHttpRequestFactory() {
            }

            public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
                ClientHttpRequest result = super.createRequest(uri, httpMethod);

                for (Map<String, String> map : credentials.getCredentials().values()) {
                    String authority = map.get("authority");
                    if (authority != null && authority.equals(uri.getAuthority())) {
                        result.getHeaders().add("token", map.get("authorization"));
                        break;
                    }
                }
                return result;
            }
        }
    }



    public static void main(String[] args) {
        SpringApplication.run(ProcurementApplication.class, args);
    }
}
