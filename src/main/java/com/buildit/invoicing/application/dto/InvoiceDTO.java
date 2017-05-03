package com.buildit.invoicing.application.dto;

import com.buildit.common.rest.ResourceSupport;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by stepan on 03/05/2017.
 */
@Data
@EqualsAndHashCode
public class InvoiceDTO extends ResourceSupport {
    String _id;
    LocalDate dueDate;
    BigDecimal amount;
}
