package com.buildit.rental.application.services;

import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.domain.dto.PurchaseOrderDTO;
import com.buildit.rental.domain.model.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Created by minhi_000 on 17.03.2017.
 */
@Service
public class RentalService {
    @Autowired
    RestTemplate restTemplate;

    public List<PlantInventoryEntryDTO> findAvailablePlants(String plantName, LocalDate startDate, LocalDate endDate) {
        PlantInventoryEntryDTO[] plants = restTemplate.getForObject(
                "http://localhost:8090/api/inventory/plants?name={name}&startDate={start}&endDate={end}",
                PlantInventoryEntryDTO[].class, plantName, startDate, endDate);
        return Arrays.asList(plants);
    }

    public PurchaseOrderDTO createPurchaseOrder(String name, LocalDate startDate, LocalDate endDate) {
        //todo: finish here
        PurchaseOrderDTO purchaseOrderDTO = restTemplate.getForObject(
                "http://localhost:8090/api/sales/orders?name={name}&startDate={start}&endDate={end}",
                PurchaseOrderDTO.class,name,startDate,endDate);

        //todo: somehow handle links?
        return purchaseOrderDTO;
    }
}
