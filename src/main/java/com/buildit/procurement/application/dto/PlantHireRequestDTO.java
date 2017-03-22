package com.buildit.procurement.application.dto;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.common.rest.ResourceSupport;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.domain.model.POStatus;
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
    //TODO DTO
    @Embedded
    BusinessPeriod rentalPeriod;
    @Embedded
    PlantInventoryEntryDTO plantInvEntryDTO;
    @Embedded
    PurchaseOrderDTO purchaseOrderDTO;


}
