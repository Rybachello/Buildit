package com.buildit.rental.application.dto;

import com.buildit.common.rest.ResourceSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RentITPlantInventoryEntryDTO extends ResourceSupport{
    String _id;
    String name;
    String description;
    BigDecimal price;
}
