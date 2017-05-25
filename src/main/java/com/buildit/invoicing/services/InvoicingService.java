package com.buildit.invoicing.services;

import com.buildit.common.application.Constants;
import com.buildit.invoicing.application.dto.InvoiceDTO;
import com.buildit.invoicing.domain.model.Invoice;
import com.buildit.invoicing.domain.repository.InvoiceRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Vasiliy on 2017-05-03.
 */
@Service
public class InvoicingService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    InvoiceRepository invoiceRepository;
    @Autowired
    InvoiceAssembler invoiceAssembler;
    @Autowired
    InvoicingService invoicingService;

    @Data
    @AllArgsConstructor
    class RemittanceAdviceDTO {
        String InvoiceId;
        String Id;
    }

    public void sendRemittanceAdvice(String POID, String invoiceId) {
        restTemplate.postForObject(
                Constants.RENTIT_URL+"api/remittances/remittance",
                new RemittanceAdviceDTO(invoiceId, POID),
                String.class);
    }

    public List<InvoiceDTO> findAllInvoices() {
        return invoiceAssembler.toResources(invoiceRepository.findAll());
    }

    public InvoiceDTO approveInvoice(String id) {
        Invoice invoice = invoiceRepository.findOne(id);

        invoice.approve();

        invoicingService.sendRemittanceAdvice(invoice.getPurchaseOrder().getPurchaseOrderId(), invoice.getId());

        invoiceRepository.flush();

        return invoiceAssembler.toResource(invoice);
    }
}
