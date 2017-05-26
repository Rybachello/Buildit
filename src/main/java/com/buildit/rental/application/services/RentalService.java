package com.buildit.rental.application.services;

import com.buildit.common.application.Constants;
import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.rental.application.dto.PurchaseOrderDTO;
import com.buildit.rental.application.dto.RentITPlantInventoryEntryDTO;
import com.buildit.rental.application.dto.RentITPurchaseOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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
                Constants.RENTIT_URL+"api/inventory/plants?name={name}&startDate={start}&endDate={end}",
                RentITPlantInventoryEntryDTO[].class, plantName, startDate, endDate);
        Arrays.stream(plants).forEach(i->i.setSupplier("t13"));
        return Arrays.asList(plants);
    }

    public RentITPurchaseOrderDTO createPurchaseOrder(String plantId, LocalDate startDate, LocalDate endDate) {
        RentITPurchaseOrderDTO reqDTO = new RentITPurchaseOrderDTO();
        reqDTO.setPlant(new RentITPlantInventoryEntryDTO(plantId,null,null,null, null));
        reqDTO.setRentalPeriod(BusinessPeriod.of(startDate, endDate));
        RentITPurchaseOrderDTO rentITPurchaseOrderDTO = restTemplate.postForObject(
                Constants.RENTIT_URL+"api/sales/orders",
                reqDTO,
                RentITPurchaseOrderDTO.class);
        return rentITPurchaseOrderDTO;
    }

    //todo: need to test this
    public List<PurchaseOrderDTO> findAllPurchaseOrders(String token)
    {
        PurchaseOrderDTO[] purchaseOrderDTOs = restTemplate.getForObject(
                Constants.RENTIT_URL+"api/sales/orders",
                              PurchaseOrderDTO[].class);
        return Arrays.asList(purchaseOrderDTOs);
    }
    public PurchaseOrderDTO cancelPurchaseOrder(String id)
    {
        ResponseEntity<PurchaseOrderDTO> response = restTemplate.exchange(
                Constants.RENTIT_URL+"api/sales/orders/{id}",
                HttpMethod.DELETE,
                null,
                PurchaseOrderDTO.class,
                id);

        return response.getBody();
    }

    public ResponseEntity<PurchaseOrderDTO> resubmittingPurchaseOrder(PurchaseOrderDTO purchaseOrder)
    {
        ResponseEntity<PurchaseOrderDTO> response = restTemplate.exchange(
                Constants.RENTIT_URL+"/api/sales/orders",
                HttpMethod.PUT,
                new HttpEntity<>(purchaseOrder),
                PurchaseOrderDTO.class);

        return response;
    }
}
