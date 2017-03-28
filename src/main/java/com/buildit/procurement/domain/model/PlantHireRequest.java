package com.buildit.procurement.domain.model;

import com.buildit.common.domain.model.BusinessPeriod;
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

    Person requestedBy; // site engineer
    Person reviewedBy; // works engineer

    BigDecimal cost;

    public void updateStatus(PHRStatus status) {
        this.status = status;
    }
}
