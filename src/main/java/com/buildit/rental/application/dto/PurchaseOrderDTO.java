package com.buildit.rental.application.dto;

import com.buildit.common.dto.BusinessPeriodDTO;
import com.buildit.rental.domain.model.POStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Rybachello on 3/22/2017.
 */
@Data
public class PurchaseOrderDTO {
    String _id;
    POStatus status;
    BusinessPeriodDTO rentalPeriod;
    BigDecimal total;
    PlantInventoryEntryDTO plant;
    String constructionSite;
}
