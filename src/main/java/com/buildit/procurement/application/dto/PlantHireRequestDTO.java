package com.buildit.procurement.application.dto;

import com.buildit.common.dto.BusinessPeriodDTO;
import com.buildit.common.rest.ResourceSupport;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Rybachello on 3/21/2017.
 */
@Data
@EqualsAndHashCode
public class PlantHireRequestDTO extends ResourceSupport {

    String _id;
    PHRStatus status;
    BusinessPeriodDTO rentalPeriod;
    PlantInventoryEntryDTO plantInvEntryDTO;
//    @Embedded
//    PurchaseOrderDTO purchaseOrderDTO;

}
