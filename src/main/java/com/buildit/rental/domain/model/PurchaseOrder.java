package com.buildit.rental.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by minhi_000 on 20.03.2017.
 */
@Embeddable
@Value
@AllArgsConstructor(staticName = "of")
@Data
public class PurchaseOrder {
    String purchaseOrderId;

    String purchaseOrderHref;

    public PurchaseOrder() {
        this.purchaseOrderHref = null;
        this.purchaseOrderId = null;
    }
}
