package com.buildit.invoicing.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

/**
 * Created by Vasiliy on 2017-05-03.
 */
@Service
public class InvoicingService {
    @Autowired
    RestTemplate restTemplate;

    @Data
    @AllArgsConstructor
    class RemittanceAdvice{
        String POID;
        BigDecimal amount;
    }

    public void sendRemittanceAdvice(String POID, BigDecimal amount) {
        restTemplate.postForObject(
                "http://localhost:8090/api/invoicing/remittanceAdvice",
                new RemittanceAdvice(POID, amount),
                String.class);
    }
}
