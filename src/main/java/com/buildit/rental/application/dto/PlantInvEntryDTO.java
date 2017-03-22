package com.buildit.rental.application.dto;

import com.buildit.common.rest.ResourceSupport;
import lombok.Data;

/**
 * Created by Rybachello on 3/22/2017.
 */
@Data
public class PlantInvEntryDTO extends ResourceSupport{

    String name;

    String planInventoryEntryHref;

    public PlantInvEntryDTO(String name,String planInventoryEntryHref){
        this.name = name;
        this.planInventoryEntryHref = planInventoryEntryHref;
    }

}
