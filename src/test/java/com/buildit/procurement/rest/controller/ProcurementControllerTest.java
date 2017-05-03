package com.buildit.procurement.rest.controller;

import com.buildit.ProcurementApplication;
import com.buildit.common.dto.BusinessPeriodDTO;
import com.buildit.procurement.application.dto.PlantHireRequestDTO;
import com.buildit.procurement.application.services.ProcurementService;
import com.buildit.procurement.domain.model.PHRStatus;
import com.buildit.procurement.domain.repository.PlantHireRequestRepository;
import com.buildit.rental.application.dto.PlantInventoryEntryDTO;
import com.buildit.rental.application.dto.RentITPlantInventoryEntryDTO;
import com.buildit.rental.application.dto.RentITPurchaseOrderDTO;
import com.buildit.rental.application.services.RentalService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ProcurementApplication.class,
        ProcurementControllerTest.RentalServiceMock.class})
@WebAppConfiguration
public class ProcurementControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("_halObjectMapper")
    ObjectMapper mapper;

    @Autowired
    RentalService rentalService;

    @Autowired
    PlantHireRequestRepository phrRepo;

    @Autowired
    ProcurementService procurementService;

    @Configuration
    static class RentalServiceMock {
        @Bean
        public RentalService rentalService() {
            return Mockito.mock(RentalService.class);
        }
    }

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testGetAllPlants() throws Exception {
        Resource responseBody = new ClassPathResource("trucks.json", this.getClass());
        List<RentITPlantInventoryEntryDTO> list =
                mapper.readValue(responseBody.getFile(), new TypeReference<List<RentITPlantInventoryEntryDTO>>() {
                });
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        when(rentalService.findAvailablePlants("Truck", startDate, endDate)).thenReturn(list);
        MvcResult result = mockMvc.perform(
                get("/api/procurements/plants?name=Truck&startDate={start}&endDate={end}", startDate, endDate))
                .andExpect(status().isOk())
                .andReturn();
        List<RentITPlantInventoryEntryDTO> rentITPlantInventoryEntryDTOS = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<RentITPlantInventoryEntryDTO>>() {
        });
        assertEquals(list, rentITPlantInventoryEntryDTOS);
    }

    @Test
    public void testCreatePOandCreatePHR() throws Exception {
        //todo: this fails
        Resource responseBody = new ClassPathResource("purchaseOrder.json", this.getClass());
        RentITPurchaseOrderDTO newlyCreated =
                mapper.readValue(responseBody.getFile(), new TypeReference<RentITPurchaseOrderDTO>() {
                });

        when(rentalService.createPurchaseOrder("1", newlyCreated.getRentalPeriod().getStartDate(), newlyCreated.getRentalPeriod().getEndDate())).thenReturn(newlyCreated);

        PlantHireRequestDTO plantHireRequestDTO = new PlantHireRequestDTO();
        plantHireRequestDTO.setRentalPeriod(BusinessPeriodDTO.of(newlyCreated.getRentalPeriod().getStartDate(), newlyCreated.getRentalPeriod().getEndDate()));
        plantHireRequestDTO.setPlantInventoryEntry(new PlantInventoryEntryDTO(newlyCreated.getPlant().get_id(), "gg", "hgh"));

        mockMvc.perform(
                post("/api/procurements/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(plantHireRequestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreatePOWithRejection() throws Exception {
        Resource responseBody = new ClassPathResource("trucks.json", this.getClass());
        List<RentITPlantInventoryEntryDTO> list =
                mapper.readValue(responseBody.getFile(), new TypeReference<List<RentITPlantInventoryEntryDTO>>() {
                });
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        when(rentalService.findAvailablePlants("Truck", startDate, endDate)).thenReturn(list);
        MvcResult result = mockMvc.perform(get("/api/procurements/plants?name=Truck&startDate={start}&endDate={end}", startDate, endDate))
                .andExpect((status().isOk()))
                .andReturn();

        List<RentITPlantInventoryEntryDTO> plantList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<RentITPlantInventoryEntryDTO>>() {
        });

        RentITPlantInventoryEntryDTO rentItPIE = plantList.get(0);

        PlantHireRequestDTO phrDTO = new PlantHireRequestDTO();
        phrDTO.set_id("1001");
        phrDTO.setPlantInventoryEntry(new PlantInventoryEntryDTO(rentItPIE.get_id(), rentItPIE.getName(), rentItPIE.getDescription()));
        phrDTO.setRentalPeriod(BusinessPeriodDTO.of(startDate, endDate));
        phrDTO.setStatus(PHRStatus.PENDING);

        MvcResult resultNewlyCreatedPHR = mockMvc.perform(post("/api/procurements/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(phrDTO)))
                .andExpect(status().isOk())
                .andReturn();

        PlantHireRequestDTO newlyCratedPHRId = mapper.readValue(resultNewlyCreatedPHR.getResponse().getContentAsString(), new TypeReference<PlantHireRequestDTO>() {
        });

        MvcResult rejectedResult = mockMvc.perform(delete("/api/procurements/requests/{id}/approve", newlyCratedPHRId.get_id())).andExpect(status().isOk()).andReturn();

        PlantHireRequestDTO rejectedPHR = mapper.readValue(rejectedResult.getResponse().getContentAsString(), new TypeReference<PlantHireRequestDTO>() {
        });

        assertEquals(PHRStatus.REJECTED,rejectedPHR.getStatus());
    }

    @Test
    public void testCreationPHRandRentItPo() throws Exception {
        Resource responseBody = new ClassPathResource("trucks.json", this.getClass());
        List<RentITPlantInventoryEntryDTO> list =
                mapper.readValue(responseBody.getFile(), new TypeReference<List<RentITPlantInventoryEntryDTO>>() {
                });
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        when(rentalService.findAvailablePlants("Truck", startDate, endDate)).thenReturn(list);
        MvcResult result = mockMvc.perform(get("/api/procurements/plants?name=Truck&startDate={start}&endDate={end}", startDate, endDate))
                .andExpect((status().isOk()))
                .andReturn();

        List<RentITPlantInventoryEntryDTO> plantList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<RentITPlantInventoryEntryDTO>>() {
        });

        RentITPlantInventoryEntryDTO rentItPIE = plantList.get(0);

        PlantHireRequestDTO phrDTO = new PlantHireRequestDTO();
        phrDTO.set_id("1001");
        phrDTO.setPlantInventoryEntry(new PlantInventoryEntryDTO(rentItPIE.get_id(), rentItPIE.getName(), rentItPIE.getDescription()));
        phrDTO.setRentalPeriod(BusinessPeriodDTO.of(startDate, endDate));
        phrDTO.setStatus(PHRStatus.PENDING);

        MvcResult resultNewlyCreatedPHR = mockMvc.perform(post("/api/procurements/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(phrDTO)))
                .andExpect(status().isOk())
                .andReturn();

        PlantHireRequestDTO newlyCratedPHRId = mapper.readValue(resultNewlyCreatedPHR.getResponse().getContentAsString(), new TypeReference<PlantHireRequestDTO>() {
        });

        MvcResult resultAcceptedPHR = mockMvc.perform(post("/api/procurements/requests/{id}/approve",newlyCratedPHRId.get_id())).andExpect(status().isOk()).andReturn();

        PlantHireRequestDTO acceptedPHR = mapper.readValue(resultAcceptedPHR.getResponse().getContentAsString(), new TypeReference<PlantHireRequestDTO>() {
        });

        assertEquals(PHRStatus.ACCEPTED,acceptedPHR.getStatus());
    }

}

