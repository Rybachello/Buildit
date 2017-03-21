package com.buildit.procurement.application.dto;

import com.buildit.common.rest.ResourceSupport;
import com.buildit.rental.domain.model.POStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by Rybachello on 3/21/2017.
 */
@Data
@EqualsAndHashCode
public class PlantHireRequestDTO extends ResourceSupport {
    //todo: complete here
    @Enumerated(EnumType.STRING)
    POStatus status;
}
