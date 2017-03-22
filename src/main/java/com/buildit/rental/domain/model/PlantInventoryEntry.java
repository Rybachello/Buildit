package com.buildit.rental.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by minhi_000 on 20.03.2017.
 */
@Embeddable
@Value
@AllArgsConstructor(staticName = "of")
public class PlantInventoryEntry {

    public PlantInventoryEntry() {
        this._id = null;
        this.planInventoryEntryHref = null;
    }

    String _id;

    String planInventoryEntryHref;

}
