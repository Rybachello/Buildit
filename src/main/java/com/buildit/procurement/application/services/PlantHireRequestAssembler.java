package com.buildit.procurement.application.services;

import com.buildit.common.application.exceptions.PlantHireRequestNotFoundException;
import com.buildit.common.dto.BusinessPeriodDTO;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repository.PlantHireRequestRepository;
import com.buildit.procurement.rest.controller.ProcurementRestController;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;
import com.buildit.common.rest.ExtendedLink;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * Created by Rybachello on 3/21/2017.
 */
@Service
public class PlantHireRequestAssembler extends ResourceAssemblerSupport<PlantHireRequest, PlantHireRequestDTO> {
    @Autowired
    PlantHireRequestRepository plantHireRequestRepository;

    public PlantHireRequestAssembler() {
        super(ProcurementRestController.class, PlantHireRequestDTO.class);
    }

    @Override
    public PlantHireRequestDTO toResource(PlantHireRequest plantHireRequest) {
        PlantHireRequestDTO plantHireRequestDTO = new PlantHireRequestDTO();
        plantHireRequestDTO.set_id(plantHireRequest.getId());
        plantHireRequestDTO.setStatus(plantHireRequest.getStatus());
        plantHireRequestDTO.setRentalPeriod(BusinessPeriodDTO.of(plantHireRequest.getRentalPeriod().getStartDate(), plantHireRequest.getRentalPeriod().getEndDate()));
        plantHireRequestDTO.setPlantInventoryEntry(new PlantInventoryEntryDTO(plantHireRequest.getId(), plantHireRequest.getPlantInventoryEntry().getPlantInventoryEntryName(), plantHireRequest.getPlantInventoryEntry().getPlantInventoryEntryHref()));
        //plantHireRequestDTO.setPurchaseOrderDTO(new PurchaseOrderDTO(plantHireRequest.getPurchaseOrder().getPurchaseOrderHref()));

        try {
            plantHireRequestDTO.add(new ExtendedLink(
                    linkTo(methodOn(ProcurementRestController.class)
                            .getPlantHireRequest(plantHireRequestDTO.get_id())).toString(),
                    "self", GET
            ));

            switch (plantHireRequestDTO.getStatus()) {
                case PENDING:
                    plantHireRequestDTO.add(new ExtendedLink(
                            linkTo(methodOn(ProcurementRestController.class)
                                    .acceptPlantHireRequest(plantHireRequestDTO.get_id())).toString(),
                            "approve", POST));
                    plantHireRequestDTO.add(new ExtendedLink(
                            linkTo(methodOn(ProcurementRestController.class)
                                    .rejectPlantHireRequest(plantHireRequestDTO.get_id())).toString(),
                            "reject", DELETE));
                    break;
//                case OPEN:
//                    plantHireRequestDTO.add(new ExtendedLink(
//                            linkTo(methodOn(ProcurementRestController.class)
//                                    .closePlantHireRequest(newDTO.get_id())).toString(),
//                            "close", DELETE));
                default:
                    break;

            }
        } catch (PlantHireRequestNotFoundException e) {
            e.printStackTrace();
        }
        return plantHireRequestDTO;
    }
}
