package com.buildit.rental.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Vasiliy on 2017-05-26.
 */

@Data
@AllArgsConstructor
public class KopylashykPlantInventoryEntryDTO {
    String _id;
    String name;
    String description;
    BigDecimal price;

    public RentITPlantInventoryEntryDTO toOur(){
        return new RentITPlantInventoryEntryDTO(_id, name, description, price, "t11");
    }
}
