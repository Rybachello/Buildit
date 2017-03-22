package com.buildit.rental.application.dto;

import com.buildit.common.rest.ResourceSupport;
import lombok.Data;

/**
 * Created by Rybachello on 3/22/2017.
 */
@Data
public class PlantInventoryEntryDTO extends ResourceSupport{

    String name;

    String planInventoryEntryHref;

    public PlantInventoryEntryDTO(String name, String planInventoryEntryHref){
        this.name = name;
        this.planInventoryEntryHref = planInventoryEntryHref;
    }

}
