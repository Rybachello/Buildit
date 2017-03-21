package com.buildit.procurement.application.services;

import com.buildit.common.domain.infastructure.IdentifierFactory;
import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.services.RentalService;
import com.buildit.rental.domain.dto.PurchaseOrderDTO;
import com.buildit.rental.domain.model.PlantHireRequest;
import com.buildit.rental.domain.model.PurchaseOrder;
import com.buildit.rental.domain.repository.PlantHireRequestRepository;
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
        return rentalService.findAvailablePlants(name,startDate,endDate);
    }

    public PlantHireRequestDTO createPlantHireRequest(String name, LocalDate startDate, LocalDate endDate) {

        PurchaseOrderDTO purchaseOrderDTO = rentalService.createPurchaseOrder(name,startDate,endDate);

        PlantHireRequest plantHireRequest = PlantHireRequest.of(IdentifierFactory.nextID(), BusinessPeriod.of(startDate,endDate),null,null,null);

        //todo: save links from rental service
        plantHireRequestRepository.save(plantHireRequest);
        //save to dto
        return plantHireRequestAssembler.toResource(plantHireRequest);
    }
}
