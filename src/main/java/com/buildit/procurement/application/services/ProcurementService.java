package com.buildit.procurement.application.services;

import com.buildit.common.application.exceptions.PlantHireRequestNotFoundException;
import com.buildit.common.application.exceptions.PurchaseOrderStatusException;
import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.procurement.domain.model.PlantHireRequest;
import com.buildit.procurement.domain.repository.PlantHireRequestRepository;
import com.buildit.procurement.infastructure.IdentifierFactory;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.application.dto.RentITPlantInventoryEntryDTO;
import com.buildit.rental.application.dto.RentITPurchaseOrderDTO;
import com.buildit.rental.application.services.RentalService;
import com.buildit.rental.domain.model.POStatus;
import com.buildit.rental.domain.model.PlantInventoryEntry;
import com.buildit.rental.domain.model.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public List<RentITPlantInventoryEntryDTO> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate) {
        return rentalService.findAvailablePlants(name, startDate, endDate);
    }

    public PlantHireRequestDTO getPlantHireRequestById(String id) throws PlantHireRequestNotFoundException {
        PlantHireRequest purchaseOrder = plantHireRequestRepository.findOne(id);
        if (purchaseOrder == null) {
            throw new PlantHireRequestNotFoundException("Purchase order not found");
        }
        return plantHireRequestAssembler.toResource(purchaseOrder);
    }

    public PlantHireRequestDTO createPlantHireRequest(PlantInventoryEntryDTO plant, LocalDate startDate, LocalDate endDate) {

        BigDecimal total = null; // change if needed
        String nextId = IdentifierFactory.nextID();
        BusinessPeriod rentalPeriod = BusinessPeriod.of(startDate, endDate);
        PHRStatus status = PHRStatus.PENDING;
        PlantInventoryEntry plantInventoryEntry = PlantInventoryEntry.of(plant.get_id(), plant.getPlanInventoryEntryHref(), plant.getName());

        PlantHireRequest plantHireRequest = PlantHireRequest.of(
                nextId,
                status,
                rentalPeriod,
                plantInventoryEntry,
                PurchaseOrder.of(null, null),
                "",
                "",
                "",
                null,
                null,
                total);
        plantHireRequestRepository.save(plantHireRequest);
        return plantHireRequestAssembler.toResource(plantHireRequest);//convert to dto
    }


    public PlantHireRequestDTO acceptPlantHireRequest(String phrId) {

        PlantHireRequest phreq = plantHireRequestRepository.findOne(phrId);
        RentITPurchaseOrderDTO rentITPurchaseOrderDTO = rentalService.createPurchaseOrder(
                phreq.getPlantInventoryEntry().get_id(),
                phreq.getRentalPeriod().getStartDate(),
                phreq.getRentalPeriod().getEndDate());
        phreq.updateStatus(PHRStatus.ACCEPTED);
        phreq.assignPO(PurchaseOrder.of(rentITPurchaseOrderDTO.get_id(), null /*rentITPurchaseOrderDTO.getId().getHref()*/));
        plantHireRequestRepository.flush();

        return plantHireRequestAssembler.toResource(phreq);
    }

    public PlantHireRequestDTO rejectPlantHireRequest(PlantHireRequestDTO plantHireRequestDTO) throws PlantHireRequestNotFoundException {
        PlantHireRequest plantHireRequest = plantHireRequestRepository.getOne(plantHireRequestDTO.get_id());

        if (plantHireRequest == null) {
            throw new PlantHireRequestNotFoundException("Plant hire request not found");
        }

        plantHireRequest.reject();

        plantHireRequestRepository.flush();

        PlantHireRequestDTO updatedDTO = plantHireRequestAssembler.toResource(plantHireRequest);

        return updatedDTO;
    }

    public PlantHireRequestDTO cancelPlantHireRequest(PlantHireRequestDTO plantHireRequestDTO) throws PlantHireRequestNotFoundException {
        PlantHireRequest plantHireRequest = plantHireRequestRepository.getOne(plantHireRequestDTO.get_id());

        if (plantHireRequest == null) {
            throw new PlantHireRequestNotFoundException("Plant hire request not found");
        }

        if (rentalService.cancelPurchaseOrder(plantHireRequest.getPurchaseOrder().getPurchaseOrderId()).getStatus() == POStatus.CLOSED) {
            plantHireRequest.cancel();
            plantHireRequestRepository.flush();
        }

        PlantHireRequestDTO updatedDTO = plantHireRequestAssembler.toResource(plantHireRequest);

        return updatedDTO;
    }


    public PlantHireRequestDTO updatePlantHireRequestById(PlantHireRequestDTO updatedDTO) throws PurchaseOrderStatusException, PlantHireRequestNotFoundException {

        PlantHireRequest plantHireRequest = plantHireRequestRepository.getOne(updatedDTO.get_id());
        if (plantHireRequest == null) {
            throw new PlantHireRequestNotFoundException("Purchase order that need to update not found");
        }

        if (plantHireRequest.getStatus() == PHRStatus.ACCEPTED) {

            PurchaseOrderDTO dto = PurchaseOrderDTO.of(plantHireRequest.getPurchaseOrder().getPurchaseOrderId(),
                    plantHireRequest.getRentalPeriod());

            ResponseEntity<PurchaseOrderDTO> responseEntity = rentalService.resubmittingPurchaseOrder(dto);

            if (responseEntity.getStatusCode().is4xxClientError() || responseEntity.getStatusCode().is5xxServerError()) {
                throw new PurchaseOrderStatusException("Purchase order was rejected");
            }
        }

        BusinessPeriod businessPeriod = BusinessPeriod.of(updatedDTO.getRentalPeriod().getStartDate(), updatedDTO.getRentalPeriod().getEndDate());
        PlantInventoryEntry plantInventoryEntry = PlantInventoryEntry.of(updatedDTO.getPlantInventoryEntry().get_id(), updatedDTO.getPlantInventoryEntry().getPlanInventoryEntryHref(), updatedDTO.getPlantInventoryEntry().getName());
        plantHireRequest.resubmit(businessPeriod, plantInventoryEntry);
        plantHireRequestRepository.flush(); //todo: do we need save here?
        return plantHireRequestAssembler.toResource(plantHireRequest);


    }

    public List<PlantHireRequestDTO> getAllPlantHireRequests() {
        List<PlantHireRequest> phrList = plantHireRequestRepository.findAll();
        return plantHireRequestAssembler.toResources(phrList);
    }

    public List<PurchaseOrderDTO> getAllPurchaseOrders(String token) {
        return rentalService.findAllPurchaseOrders(token);
    }


}
