package com.buildit.rental.domain.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import javax.persistence.Embeddable;

/**
 * Created by minhi_000 on 20.03.2017.
 */
@Embeddable
@Value
@AllArgsConstructor(staticName = "of")
public class PlantInventoryEntry {

    public PlantInventoryEntry() {
        this._id = null;
        this.plantInventoryEntryHref = null;
        this.plantInventoryEntryName = null;
    }

    String _id;

    String plantInventoryEntryHref;

    String plantInventoryEntryName;

}
