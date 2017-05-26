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
public class KopylashykPurchaseOrderDTO extends ResourceSupport {

    private String _id;
    private KopylashykPlantInventoryEntryDTO plant;
    private BusinessPeriod rentalPeriod;
    private BigDecimal total;
    private POStatus status;

    public RentITPurchaseOrderDTO toOur() {
        RentITPurchaseOrderDTO our = new RentITPurchaseOrderDTO();
        our.set_id(_id);
        our.setPlant(plant.toOur());
        our.setRentalPeriod(rentalPeriod);
        our.setName(plant.getName());
        our.setStatus(status);
        our.setTotal(total);
        return our;
    }
}
