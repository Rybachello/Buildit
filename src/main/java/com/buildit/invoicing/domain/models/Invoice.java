package com.buildit.invoicing.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Vasiliy on 2017-05-03.
 */
@Getter
@Entity
@AllArgsConstructor
public class Invoice
{
    @Id
    String id;
    boolean approved;
    String note;
    LocalDate dueDate;
    LocalDate paidDate;
    BigDecimal amount;
}
