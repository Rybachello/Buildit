package com.buildit.rental.application.services;

import com.buildit.common.application.Constants;
import com.buildit.common.domain.model.BusinessPeriod;
import com.buildit.rental.application.dto.KopylashykCreatePurchaseOrderDTO;
import com.buildit.rental.application.dto.KopylashykPlantInventoryEntryDTO;
import com.buildit.rental.application.dto.KopylashykPurchaseOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vasiliy on 2017-05-26.
 */
@Service
public class KopylashykRentalService {
        @Autowired
        RestTemplate restTemplate;

        public List<KopylashykPlantInventoryEntryDTO> findAvailablePlants(String plantName, LocalDate startDate, LocalDate endDate) {
            KopylashykPlantInventoryEntryDTO[] plants = restTemplate.getForObject(
                    Constants.KOPYLASHYK_RENTIT_URL+"api/inventory/plants?name={name}&startDate={start}&endDate={end}",
                    KopylashykPlantInventoryEntryDTO[].class, plantName, startDate, endDate);
            return Arrays.asList(plants);
        }

        public KopylashykPurchaseOrderDTO createPurchaseOrder(String plantId, LocalDate startDate, LocalDate endDate) {
            KopylashykCreatePurchaseOrderDTO reqestDTO = new KopylashykCreatePurchaseOrderDTO();
            reqestDTO.setPlantId(plantId);
            reqestDTO.setRentalPeriod(BusinessPeriod.of(startDate, endDate));
            KopylashykPurchaseOrderDTO purchaseOrderDTO = restTemplate.postForObject(
                    Constants.KOPYLASHYK_RENTIT_URL+"api/sales/orders",
                    reqestDTO,
                    KopylashykPurchaseOrderDTO.class);
            return purchaseOrderDTO;
        }

//        public List<PurchaseOrder> findAllPurchaseOrders()
//        {
//            RentITPurchaseOrderDTO rentITPurchaseOrderDTO = restTemplate.getForObject(
//                    Constants.RENTIT_URL+"api/sales/orders",
//                    RentITPurchaseOrderDTO.class);
//            //return rentITPurchaseOrderDTO;
//            return null;
//        }
//        public PurchaseOrderDTO cancelPurchaseOrder(String id)
//        {
//            ResponseEntity<PurchaseOrderDTO> response = restTemplate.exchange(
//                    Constants.RENTIT_URL+"api/sales/orders/{id}",
//                    HttpMethod.DELETE,
//                    null,
//                    PurchaseOrderDTO.class,
//                    id);
//
//            return response.getBody();
//        }
//
//        public PurchaseOrder resubmittingPurchaseOrder()
//        {
//            //todo:resubmitting POs
//            return null;
//        }
    }

