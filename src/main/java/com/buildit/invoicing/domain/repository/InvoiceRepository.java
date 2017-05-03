package com.buildit.invoicing.domain.repository;

import com.buildit.invoicing.domain.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    public List<Invoice> findByAccepted(Boolean accepted);
}
