package com.buildit.invoicing.services;

import com.buildit.invoicing.application.dto.InvoiceDTO;
import com.buildit.invoicing.domain.models.Invoice;
import com.buildit.invoicing.domain.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by stepan on 03/05/2017.
 */
@Service
public class InvoicingService {
    @Autowired
    InvoiceRepository invoiceRepository;
    @Autowired
    InvoiceAssembler invoiceAssembler;

    public List<InvoiceDTO> findPendingInvoices() {
        return invoiceAssembler.toResources(invoiceRepository.findByAccepted(true));
    }

    public InvoiceDTO acceptInvoice(String id) {
        Invoice invoice = invoiceRepository.findOne(id);

        invoice.accept();

        invoiceRepository.flush();

        return invoiceAssembler.toResource(invoice);
    }
}
