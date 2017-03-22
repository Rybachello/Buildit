package com.buildit.procurement.application.services;

import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repository.PlantHireRequestRepository;
import com.buildit.rental.application.dto.PlantInvEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrdrDTO;
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
    public PlantHireRequestDTO toResource(PlantHireRequest plantHireRequest) {
        PlantHireRequestDTO plantHireRequestDTO = new PlantHireRequestDTO();

        plantHireRequestDTO.setStatus(plantHireRequest.getStatus());
        plantHireRequestDTO.setRentalPeriod(plantHireRequest.getRentalPeriod());
        plantHireRequestDTO.setPlantInvEntryDTO(new PlantInvEntryDTO(plantHireRequest.getPlantInventoryEntry().getName(),plantHireRequest.getPlantInventoryEntry().getPlanInventoryEntryHref()));
        plantHireRequestDTO.setPurchaseOrdrDTO(new PurchaseOrdrDTO(plantHireRequest.getPurchaseOrder().getPurchaseOrderHref()));

        return plantHireRequestDTO;
    }
}
