package com.buildit.procurement.application.controller;

import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.application.services.ProcurementService;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created by lgarcia on 3/16/2017.
 */
@RestController
@RequestMapping("/api/procurements")
public class ProcurementRestController {

    @Autowired
    ProcurementService procurementService;


    @GetMapping("/plants")
    public List<PlantInventoryEntryDTO> findAvailablePlants(
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

    @PostMapping("/orders")
    public PlantHireRequestDTO createPlantHireRequest(
            @RequestParam(name = "name", required = false) Optional<String> plantName,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate) {
            return procurementService.createPlantHireRequest(plantName.get(),startDate.get(),endDate.get());
    }


}
