package com.buildit.invoicing.domain.model;

import com.buildit.rental.domain.model.PurchaseOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Vasiliy on 2017-05-03.
 */
@Data
@Entity
@AllArgsConstructor
public class Invoice {
    @Id
    String id;

    boolean approved;

    String note;

    LocalDate dueDate;

    LocalDate paidDate;

    BigDecimal amount;

    PurchaseOrder purchaseOrder;

    public Invoice(){}
}
