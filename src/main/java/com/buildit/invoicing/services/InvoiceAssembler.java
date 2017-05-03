package com.buildit.invoicing.services;

import com.buildit.common.application.exceptions.InvoiceNotFoundException;
import com.buildit.common.rest.ExtendedLink;
import com.buildit.invoicing.application.dto.InvoiceDTO;
import com.buildit.invoicing.domain.models.Invoice;
import com.buildit.invoicing.domain.repository.InvoiceRepository;
import com.buildit.invoicing.rest.controller.InvoicingRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by stepan on 03/05/2017.
 */
public class InvoiceAssembler extends ResourceAssemblerSupport<Invoice, InvoiceDTO> {
    @Autowired
    InvoiceRepository invoiceRepository;

    public InvoiceAssembler() {
        super(InvoicingRestController.class, InvoiceDTO.class);
    }

    @Override
    public InvoiceDTO toResource(Invoice invoice) {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.set_id(invoice.getId());
        invoiceDTO.setDueDate(invoice.getDueDate());
        invoiceDTO.setAmount(invoice.getAmount());

        try {
            if (invoice.isApproved()) {
                invoiceDTO.add(new ExtendedLink(
                        linkTo(methodOn(InvoicingRestController.class)
                                .acceptInvoice(invoiceDTO.get_id())).toString(), "accept", POST
                ));
            }
        }
        catch (InvoiceNotFoundException ex) {
            ex.printStackTrace();
        }

        return invoiceDTO;
    }
}
