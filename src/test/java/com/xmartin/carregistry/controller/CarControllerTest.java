package com.xmartin.carregistry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmartin.carregistry.domain.Brand;
import com.xmartin.carregistry.domain.Car;
import com.xmartin.carregistry.filter.JwtAuthenticationFilter;
import com.xmartin.carregistry.controller.dtos.BrandResponse;
import com.xmartin.carregistry.controller.dtos.CarRequest;
import com.xmartin.carregistry.controller.dtos.CarResponse;
import com.xmartin.carregistry.controller.mappers.BrandMapper;
import com.xmartin.carregistry.controller.mappers.CarMapper;
import com.xmartin.carregistry.service.impl.BrandServiceImpl;
import com.xmartin.carregistry.service.impl.CarServiceImpl;
import com.xmartin.carregistry.service.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CarController.class)
//@AutoConfigureMockMvc(addFilters = false)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarServiceImpl carService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter authenticationFilter;

    @MockBean
    private BrandServiceImpl brandService;

    @MockBean
    private BrandMapper brandMapper;

    @MockBean
    private CarMapper carMapper;


    CarRequest carRequest;
    Car car;
    BrandResponse brandResponse;
    CarResponse carResponse;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        carRequest = CarRequest.builder().brand("Opel")
                .model("Corsa").colour("Red").price(12000.0)
                .description("Cute").fuelType("Diesel").mileage(50).numDoors(4)
                .year(2009).build();

        car = Car.builder().id(1).brand(Brand.builder().id(1)
                        .name("Opel").country("Deutchland").warranty(3).build())
                .model("Corsa").colour("Red").price(12000.0)
                .description("Cute").fuelType("Diesel").mileage(50).numDoors(4).year(2009).build();

        brandResponse = new BrandResponse(1, "Opel", 2, "Deutchland");

        carResponse = CarResponse.builder().id(1).brand(brandResponse).model("Corsa")
                .price(15000.0).year(2010).colour("Red").description("")
                .fuelType("Diesel").mileage(2000).numDoors(4).build();

    }


    @Test
    @WithMockUser(username = "xavi@test.com", roles = "ADMIN")
    void addCarTest_valid() throws Exception {

        //given
        when(carMapper.toModel(carRequest)).thenReturn(car);
        when(carService.addCar(car)).thenReturn(car);
        when(carMapper.toResponse(car)).thenReturn(carResponse);

        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(carRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "xavi@test.com", roles = "USER")
    void getCarByIdTest_valid() throws Exception {

        //given
        when(carService.getCarById(1)).thenReturn(car);
        when(carMapper.toResponse(car)).thenReturn(carResponse);


        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/cars/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "xavi@test.com", roles = "USER")
    void getCarsTest_valid() throws Exception {

        List<Car> carList = List.of(car);

        //given
        when(carService.getCars(PageRequest.of(0, 10))).thenReturn(CompletableFuture.completedFuture(carList));
        when(carMapper.toResponseList(carList)).thenReturn(List.of(carResponse));


        //when - then
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carList").isArray())
                .andExpect(jsonPath("$.carList[0].id").value(1));

    }

    @Test
    @WithMockUser(username = "xavi@test.com", roles = "USER")
    void addCarsTest_valid() throws Exception {

        List<CarRequest> carRequestList = List.of(carRequest);
        List<Car> carList = List.of(car);

        //given
        when(carService.addCars(anyList())).thenReturn(CompletableFuture.completedFuture(carList));
        when(carMapper.toResponseList(carList)).thenReturn(List.of(carResponse));


        //when - then
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/cars/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(carRequestList)))
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void deleteCarTest_valid() throws Exception {


        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/cars/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(carRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Car with id 1 deleted"));
    }


    @Test
    @WithMockUser(username = "xavi@test.com", roles = "ADMIN")
    void updateCarTest_valid() throws Exception {

        Car updatedCar = Car.builder().id(1).brand(Brand.builder().id(1)
                        .name("Opel").country("Japan").warranty(3).build())
                .model("Corsa").colour("Black").price(12000.0)
                .description("Cool").fuelType("Diesel").mileage(50).numDoors(4).year(2020).build();

        CarResponse updatedCarResponse = CarResponse.builder().id(1).brand(BrandResponse.builder().id(1)
                        .name("Opel").country("Japan").warranty(3).build())
                .model("Corsa").colour("Black").price(12000.0)
                .description("Cool").fuelType("Diesel").mileage(50).numDoors(4).year(2020).build();

        //given
        when(carMapper.toModel(carRequest)).thenReturn(updatedCar);
        when(carService.updateCar(updatedCar, 1)).thenReturn(updatedCar);
        when(carMapper.toResponse(updatedCar)).thenReturn(updatedCarResponse);

        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/cars/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(carRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Cool"))
                .andExpect(jsonPath("$.year").value(2020));
    }
}
