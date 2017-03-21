package com.buildit.procurement.application.services;

import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.rental.domain.model.PlantHireRequest;
import com.buildit.rental.domain.repository.PlantHireRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

/**
 * Created by Rybachello on 3/21/2017.
 */
@Service
public class PlantHireRequestAssembler extends ResourceAssemblerSupport<PlantHireRequest, PlantHireRequestDTO> {
    @Autowired
    PlantHireRequestRepository plantHireRequestRepository;

    public PlantHireRequestAssembler() {
        super(PlantHireRequest.class, PlantHireRequestDTO.class);
    }

    @Override
    public PlantHireRequestDTO toResource(PlantHireRequest purchaseOrder) {
        //todo: finish here
        return null;
    }
}
