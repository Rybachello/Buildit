package com.buildit.invoicing.rest.controller;

import com.buildit.common.application.exceptions.InvoiceNotFoundException;
import com.buildit.invoicing.application.dto.InvoiceDTO;
import com.buildit.invoicing.services.InvoicingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by stepan on 03/05/2017.
 */
@CrossOrigin()
@RestController
@RequestMapping("/api/invoicing")
public class InvoicingRestController {
    @Autowired
    InvoicingService invoicingService;

    @GetMapping("/invoices")
    public List<InvoiceDTO> getPendingInvoices() {
        return invoicingService.findAllInvoices();
    }

    @ExceptionHandler(InvoiceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleInvoiceNotFoundException(InvoiceNotFoundException ex) {}

    @PostMapping("/invoices/{id}/approve")
    public ResponseEntity<InvoiceDTO> approveInvoice(@PathVariable String id) throws InvoiceNotFoundException {
        InvoiceDTO invoiceDTO = invoicingService.approveInvoice(id);

        return new ResponseEntity<InvoiceDTO>(invoiceDTO, HttpStatus.OK);
    }
}
