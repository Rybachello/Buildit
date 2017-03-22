package com.buildit.rental.application.dto;

import com.buildit.common.rest.ResourceSupport;
import lombok.Data;

/**
 * Created by Rybachello on 3/22/2017.
 */
@Data
public class PurchaseOrderDTO extends ResourceSupport {
    String purchaseOrderHref;
    public PurchaseOrderDTO(String purchaseOrderHref){
        this.purchaseOrderHref = purchaseOrderHref;
    }

}
