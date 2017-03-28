package com.buildit.procurement.application.services;

import com.buildit.common.application.exceptions.InvalidPlantHireRequestStatusException;
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
import com.buildit.common.rest.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;

import static org.springframework.http.HttpMethod.POST;

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
        plantHireRequestDTO.setRentalPeriod(BusinessPeriodDTO.of(plantHireRequest.getRentalPeriod().getStartDate(), plantHireRequest.getRentalPeriod().getEndDate()));
        plantHireRequestDTO.setPlantInvEntryDTO(new PlantInventoryEntryDTO(plantHireRequest.getId(), plantHireRequest.getPlantInventoryEntry().get_id(), plantHireRequest.getPlantInventoryEntry().getPlanInventoryEntryHref()));
        //plantHireRequestDTO.setPurchaseOrderDTO(new PurchaseOrderDTO(plantHireRequest.getPurchaseOrder().getPurchaseOrderHref()));

        try {
            switch (plantHireRequestDTO.getStatus()) {
                case PENDING:
                    plantHireRequestDTO.add(new ExtendedLink(
                            linkTo(methodOn(ProcurementRestController.class)
                                    .acceptPlantHireRequest(plantHireRequestDTO.get_id())).toString(),
                            "accept", POST));
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
        } catch (Exception e) {
        } catch (PlantHireRequestNotFoundException e) {
            e.printStackTrace();
        }
        return plantHireRequestDTO;
    }
}
