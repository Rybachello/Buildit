package com.buildit.procurement.domain.model;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.common.dto.BusinessPeriodDTO;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.domain.model.POStatus;
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
    PlantInventoryEntry plantInventoryEntry;
    @Embedded
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

    public void reject() {
        this.status = PHRStatus.REJECTED;
    }

    public void resubmit(BusinessPeriod rentalPeriod, PlantInventoryEntry plantInventoryEntry) {
        this.rentalPeriod = rentalPeriod;
        this.plantInventoryEntry = plantInventoryEntry;
        this.status = PHRStatus.PENDING;
    }
}
