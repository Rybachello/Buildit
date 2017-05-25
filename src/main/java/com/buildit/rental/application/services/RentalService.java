package com.buildit.rental.application.services;

import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.application.dto.RentITPlantInventoryEntryDTO;
import com.buildit.rental.application.dto.RentITPurchaseOrderDTO;
import com.buildit.rental.domain.model.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    public RentITPurchaseOrderDTO createPurchaseOrder(String plantId, LocalDate startDate, LocalDate endDate) {
        RentITPurchaseOrderDTO reqDTO = new RentITPurchaseOrderDTO();
        reqDTO.setPlant(new RentITPlantInventoryEntryDTO(plantId,null,null,null));
        reqDTO.setRentalPeriod(BusinessPeriod.of(startDate, endDate));
        RentITPurchaseOrderDTO rentITPurchaseOrderDTO = restTemplate.postForObject(
                "http://localhost:8090/api/sales/orders",
                reqDTO,
                RentITPurchaseOrderDTO.class);
        return rentITPurchaseOrderDTO;
    }
    public List<PurchaseOrder> findAllPurchaseOrders()
    {
        RentITPurchaseOrderDTO rentITPurchaseOrderDTO = restTemplate.getForObject(
                "http://localhost:8090/api/sales/orders",
                RentITPurchaseOrderDTO.class);
        //return rentITPurchaseOrderDTO;
        return null;
    }
    public PurchaseOrderDTO cancelPurchaseOrder(String id)
    {
        ResponseEntity<PurchaseOrderDTO> response = restTemplate.exchange(
                "http://localhost:8090/api/sales/orders/{id}",
                HttpMethod.DELETE,
                null,
                PurchaseOrderDTO.class,
                id);

        return response.getBody();
    }

    public PurchaseOrder resubmittingPurchaseOrder()
    {
        //todo:resubmitting POs
        return null;
    }
}
