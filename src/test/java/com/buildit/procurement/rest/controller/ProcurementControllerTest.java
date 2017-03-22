package com.buildit.procurement.rest.controller;

import com.buildit.ProcurementApplication;
import com.buildit.rental.application.dto.RentITPlantInventoryEntryDTO;
import com.buildit.rental.application.services.RentalService;
import com.buildit.rental.application.dto.RentITPurchaseOrderDTO;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void testCreationPlanHireRequest() throws Exception {
        Resource responseBody = new ClassPathResource("purchaseOrder.json", this.getClass());
        RentITPurchaseOrderDTO mockedPurchaseOrderDTO =
                mapper.readValue(responseBody.getFile(), new TypeReference<RentITPurchaseOrderDTO>() {
                });
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        when(rentalService.createPurchaseOrder("Truck", startDate, endDate)).thenReturn(mockedPurchaseOrderDTO);
        MvcResult result = mockMvc.perform(
                get("/api/procurements/plants?name=Truck&startDate={start}&endDate={end}", startDate, endDate))
                .andExpect(status().isOk())
                .andReturn();
        RentITPurchaseOrderDTO rentITPurchaseOrderDTO = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RentITPurchaseOrderDTO>() {
        });
        assertEquals(mockedPurchaseOrderDTO, rentITPurchaseOrderDTO);

    }
}

