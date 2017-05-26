package com.buildit.rental.application;

import com.buildit.rental.application.dto.KopylashykPlantInventoryEntryDTO;
import com.buildit.rental.application.dto.RentITPlantInventoryEntryDTO;
import com.buildit.rental.application.dto.RentITPurchaseOrderDTO;
import com.buildit.rental.application.services.KopylashykRentalService;
import com.buildit.rental.application.services.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalMediator {
    @Autowired
    private RentalService rentalService;

    @Autowired
    private KopylashykRentalService kopylashykRentalService;

    public List<RentITPlantInventoryEntryDTO> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate) {
        List<RentITPlantInventoryEntryDTO> our = rentalService.findAvailablePlants(name, startDate, endDate);
        List<KopylashykPlantInventoryEntryDTO> k;
        try {
            k = kopylashykRentalService.findAvailablePlants(name, startDate, endDate);
        } catch (Exception e) {
          k = new ArrayList<>();
        }

        List<RentITPlantInventoryEntryDTO> all = new ArrayList<>();
        all.addAll(our);
        all.addAll(k.stream().map(i -> i.toOur()).collect(Collectors.toList()));
        return all;
    }

    public RentITPurchaseOrderDTO createPurchaseOrder(String plantId, LocalDate startDate, LocalDate endDate, String supplier) {

        switch (supplier){
            case "t13":
                return rentalService.createPurchaseOrder(plantId, startDate, endDate);
            case "t11":
                return kopylashykRentalService.createPurchaseOrder(plantId, startDate, endDate).toOur();
        }

        return null;
    }

}
