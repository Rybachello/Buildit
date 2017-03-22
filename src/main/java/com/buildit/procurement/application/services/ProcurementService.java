package com.buildit.procurement.application.services;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.common.rest.ExtendedLink;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.infastructure.IdentifierFactory;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.services.RentalService;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.domain.model.POStatus;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.rental.domain.model.PurchaseOrder;
import com.buildit.procurement.domain.repository.PlantHireRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Rybachello on 3/21/2017.
 */
@Service
public class ProcurementService {

    @Autowired
    RentalService rentalService;
    @Autowired
    PlantHireRequestRepository plantHireRequestRepository;
    @Autowired
    PlantHireRequestAssembler plantHireRequestAssembler;

    public List<PlantInventoryEntryDTO> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate) {
        return rentalService.findAvailablePlants(name, startDate, endDate);
    }

    public PlantHireRequestDTO createPlantHireRequest(String name, LocalDate startDate, LocalDate endDate) {

        PurchaseOrderDTO purchaseOrderDTO = rentalService.createPurchaseOrder(name, startDate, endDate);

        String nextId = IdentifierFactory.nextID();
        BusinessPeriod rentalPeriod = BusinessPeriod.of(startDate, endDate);
        POStatus status = purchaseOrderDTO.getStatus();
        //todo: what the name in params?
        //todo: what the links ???
        List<ExtendedLink> links = purchaseOrderDTO.get_xlinks();
        PlantInventoryEntry plantInventoryEntry = PlantInventoryEntry.of(name, links.get(0).toString());
        //todo: need to put right link
        PurchaseOrder purchaseOrder = PurchaseOrder.of(links.get(1).toString());
        //create PHR
        PlantHireRequest plantHireRequest = PlantHireRequest.of(nextId, rentalPeriod, status, plantInventoryEntry, purchaseOrder);

        plantHireRequestRepository.save(plantHireRequest);
        return plantHireRequestAssembler.toResource(plantHireRequest);//convert to dto
    }
}
