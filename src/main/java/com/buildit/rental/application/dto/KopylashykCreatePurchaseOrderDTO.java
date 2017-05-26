package com.buildit.rental.application.dto;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.common.rest.ResourceSupport;
import com.buildit.rental.domain.model.POStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Rybachello on 3/21/2017.
 */
@Data
public class KopylashykCreatePurchaseOrderDTO {

    private String plantId;
    private BusinessPeriod rentalPeriod;
    private String constructionSite;
}
