package com.buildit.procurement.domain.model;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.rental.domain.model.PurchaseOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by minhi_000 on 20.03.2017.
 */
@Entity
@Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class PlantHireRequest {
    @Id
    String id;

    @Enumerated(EnumType.STRING)
    PHRStatus status;

    @Embedded
    BusinessPeriod rentalPeriod;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="name", column=@Column(name="plant_inventory_entry_name")),
            @AttributeOverride(name="href", column=@Column(name="plant_inventory_entry_href"))
    })
    PlantInventoryEntry plantInventoryEntry;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="href", column=@Column(name="purchase_order_href"))
    })

    PurchaseOrder purchaseOrder;

    String constructionSite;
    String supplier;
    String comments;

    @ManyToOne
    Person requestedBy; // site engineer
    @ManyToOne
    Person reviewedBy; // works engineer

    BigDecimal cost;


    public void updateStatus(PHRStatus status) {
        this.status = status;
    }

    public void assignPO(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public void reject() {
        this.status = PHRStatus.REJECTED;
    }

    public void cancel() {
        this.status = PHRStatus.CLOSED;
    }

    public void resubmit(BusinessPeriod rentalPeriod, PlantInventoryEntry plantInventoryEntry) {
        this.rentalPeriod = rentalPeriod;
        this.plantInventoryEntry = plantInventoryEntry;
        this.status = PHRStatus.PENDING;
    }
}
