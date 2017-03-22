package com.buildit.rental.application.dto;

import com.buildit.common.rest.ResourceSupport;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RentITPlantInventoryEntryDTO extends ResourceSupport{
    String _id;
    String name;
    String description;
    BigDecimal price;
}
