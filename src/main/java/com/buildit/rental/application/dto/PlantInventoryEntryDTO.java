package com.buildit.rental.application.dto;

import com.buildit.common.rest.ResourceSupport;
import lombok.Data;

/**
 * Created by Rybachello on 3/22/2017.
 */
@Data
public class PlantInventoryEntryDTO extends ResourceSupport{

    String _id;

    String name;

    String planInventoryEntryHref;

    String supplier;

    public PlantInventoryEntryDTO(){}

    public PlantInventoryEntryDTO(String id, String name, String planInventoryEntryHref){
        this._id = id;
        this.name = name;
        this.planInventoryEntryHref = planInventoryEntryHref;
    }

}
