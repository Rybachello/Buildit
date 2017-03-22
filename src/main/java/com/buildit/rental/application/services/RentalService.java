package com.buildit.rental.application.services;

import com.buildit.rental.application.dto.RentITPlantInventoryEntryDTO;
import com.buildit.rental.application.dto.RentITPurchaseOrderDTO;
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

    public List<RentITPlantInventoryEntryDTO> findAvailablePlants(String plantName, LocalDate startDate, LocalDate endDate) {
        RentITPlantInventoryEntryDTO[] plants = restTemplate.getForObject(
                "http://localhost:8090/api/inventory/plants?name={name}&startDate={start}&endDate={end}",
                RentITPlantInventoryEntryDTO[].class, plantName, startDate, endDate);
        return Arrays.asList(plants);
    }

    public RentITPurchaseOrderDTO createPurchaseOrder(String name, LocalDate startDate, LocalDate endDate) {
        RentITPurchaseOrderDTO rentITPurchaseOrderDTO = restTemplate.getForObject(
                "http://localhost:8090/api/sales/orders?name={name}&startDate={start}&endDate={end}",
                RentITPurchaseOrderDTO.class,name,startDate,endDate);
        return rentITPurchaseOrderDTO;
    }
    public List<PurchaseOrder> findAllPurchaseOrders()
    {
        //todo:querying POs
        return null;
    }
    public PurchaseOrder closePurchaseOrder()
    {
        //todo:closing POs
        return null;
    }
    public PurchaseOrder resubmittingPurchaseOrder()
    {
        //todo:resubmitting POs
        return null;
    }
}
