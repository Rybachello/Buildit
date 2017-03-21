package com.buildit.procurement.application.dto;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.common.rest.ResourceSupport;
import com.buildit.rental.domain.model.POStatus;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.rental.domain.model.PurchaseOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by Rybachello on 3/21/2017.
 */
@Data
@EqualsAndHashCode
public class PlantHireRequestDTO extends ResourceSupport {

    @Enumerated(EnumType.STRING)
    POStatus status;
    @Embedded
    BusinessPeriod rentalPeriod;
    @Embedded
    PlantInventoryEntry plantInventoryEntry;
    @Embedded
    PurchaseOrder purchaseOrder;

}
