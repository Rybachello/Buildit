package com.buildit.procurement.rest.controller;

import com.buildit.common.application.exceptions.PlantHireRequestNotFoundException;
import com.buildit.common.application.exceptions.PurchaseOrderStatusException;
import com.buildit.common.dto.BusinessPeriodDTO;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.application.services.ProcurementService;
import com.buildit.rental.application.dto.RentITPlantInventoryEntryDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created by lgarcia on 3/16/2017.
 */
@CrossOrigin()
@RestController
@RequestMapping("/api/procurements")
public class ProcurementRestController {

    @Autowired
    ProcurementService procurementService;

    @Autowired
    @Qualifier("objectMapper")
    ObjectMapper objectMapper;

    @CrossOrigin
    @GetMapping("/plants")
    public List<RentITPlantInventoryEntryDTO> findAvailablePlants(
            @RequestParam(name = "name", required = false) Optional<String> plantName,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate) {

        if (plantName.isPresent() && startDate.isPresent() && endDate.isPresent()) {
            if (endDate.get().isBefore(startDate.get()))
                throw new IllegalArgumentException("Something wrong with the requested period ('endDate' happens before 'startDate')");
            return procurementService.findAvailablePlants(plantName.get(), startDate.get(), endDate.get());
        } else
            throw new IllegalArgumentException(
                    String.format("Wrong number of parameters: Name='%s', Start date='%s', End date='%s'",
                            plantName.get(), startDate.get(), endDate.get()));
    }

    @ExceptionHandler(PlantHireRequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handPlantNotFoundException(PlantHireRequestNotFoundException ex) {
    }

    @ExceptionHandler(PurchaseOrderStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handPurchaseOrderStatusException(PurchaseOrderStatusException ex) {

    }

    @GetMapping("/requests")
    public String getAllPlantHireRequests() throws JsonProcessingException {
        return objectMapper.writeValueAsString(procurementService.getAllPlantHireRequests());
    }

    @PostMapping("/requests")
    public ResponseEntity<PlantHireRequestDTO> createPlantHireRequest(
            @RequestBody PlantHireRequestDTO request) {
        BusinessPeriodDTO period = request.getRentalPeriod();
        PlantHireRequestDTO plantHireRequest = procurementService.createPlantHireRequest(
                request.getPlantInventoryEntry(),
                period.getStartDate(),
                period.getEndDate());

        return new ResponseEntity<>(plantHireRequest, HttpStatus.OK);
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<PlantHireRequestDTO> getPlantHireRequest(@PathVariable String id) throws PlantHireRequestNotFoundException {
        PlantHireRequestDTO plantHireRequestDTO = procurementService.getPlantHireRequestById(id);

        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<PlantHireRequestDTO>(plantHireRequestDTO, headers, HttpStatus.OK);
    }

    @PutMapping("/requests/{id}")
    public ResponseEntity<PlantHireRequestDTO> updatePlantHireRequest(@RequestBody PlantHireRequestDTO updatedDTO) throws PlantHireRequestNotFoundException, PurchaseOrderStatusException {
        PlantHireRequestDTO plantHireRequestDTO = procurementService.updatePlantHireRequestById(updatedDTO);

        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<PlantHireRequestDTO>(plantHireRequestDTO, headers, HttpStatus.OK);
    }

    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<PlantHireRequestDTO> acceptPlantHireRequest(@PathVariable String id) {
        PlantHireRequestDTO plantHireRequestDTO = procurementService.acceptPlantHireRequest(id);

        return new ResponseEntity<PlantHireRequestDTO>(plantHireRequestDTO, HttpStatus.OK);
    }

    @DeleteMapping("/requests/{id}/approve")
    public ResponseEntity<PlantHireRequestDTO> rejectPlantHireRequest(@PathVariable(name = "id") String id) throws PlantHireRequestNotFoundException {
        PlantHireRequestDTO plantHireRequestDTO = procurementService.getPlantHireRequestById(id);

        PlantHireRequestDTO updatedDTO = procurementService.rejectPlantHireRequest(plantHireRequestDTO);

        return new ResponseEntity<PlantHireRequestDTO>(updatedDTO, HttpStatus.OK);
    }

    @DeleteMapping("/requests/{id}")
    public ResponseEntity<PlantHireRequestDTO> cancelPlantHireRequest(@PathVariable(name = "id") String id) throws PlantHireRequestNotFoundException {
        PlantHireRequestDTO plantHireRequestDTO = procurementService.getPlantHireRequestById(id);

        PlantHireRequestDTO updatedDTO = procurementService.cancelPlantHireRequest(plantHireRequestDTO);

        return new ResponseEntity<PlantHireRequestDTO>(updatedDTO, HttpStatus.OK);
    }

    //todo: need remove this token?
    @GetMapping("/orders")
    public String getAllPurchaseOrders(@RequestHeader String token) throws JsonProcessingException {
        return objectMapper.writeValueAsString(procurementService.getAllPurchaseOrders(token));
    }

}
