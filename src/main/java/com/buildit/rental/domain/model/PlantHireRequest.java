package com.buildit.rental.domain.model;

import com.buildit.common.domain.model.BusinessPeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

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
