package com.buildit.procurement.domain.model;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.rental.domain.model.POStatus;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.rental.domain.model.PurchaseOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;

/**
 * Created by minhi_000 on 20.03.2017.
 */
@Entity
@Getter
@AllArgsConstructor(staticName = "of")
public class PlantHireRequest {
    @Id
    String id;

    BusinessPeriod rentalPeriod;

    @Enumerated(EnumType.STRING)
    POStatus status;

    @Embedded
    PlantInventoryEntry plantInventoryEntry;

    @Embedded
    PurchaseOrder purchaseOrder;

}
