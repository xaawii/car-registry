package com.xmartin.carregistry.controller.mappers;

import com.xmartin.carregistry.domain.Brand;
import com.xmartin.carregistry.domain.Car;
import com.xmartin.carregistry.controller.dtos.BrandResponse;
import com.xmartin.carregistry.controller.dtos.CarRequest;
import com.xmartin.carregistry.controller.dtos.CarResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarMapperTest {
    @InjectMocks
    private CarMapper carMapper;

    @Mock
    private BrandMapper brandMapper;

    Car carModel;
    CarResponse carResponse;
    CarRequest carRequest;
    Brand brandModel;
    BrandResponse brandResponse;

    @BeforeEach
    void setUp() {

        brandModel = Brand.builder().name("Opel").build();
        brandResponse = new BrandResponse(1, "Opel", 2, "Deutchland");
        carModel = Car.builder().id(1).brand(brandModel).model("Corsa")
                .price(15000.0).year(2010).colour("Red").description("")
                .fuelType("Diesel").mileage(2000).numDoors(4).build();
        carResponse = CarResponse.builder().id(1).brand(brandResponse).model("Corsa")
                .price(15000.0).year(2010).colour("Red").description("")
                .fuelType("Diesel").mileage(2000).numDoors(4).build();

        carRequest = CarRequest.builder().brand("Opel")
                .model("Corsa").colour("Red").price(12000.0)
                .description("Cute").fuelType("Diesel").mileage(50).numDoors(4)
                .year(2010).build();

    }

    @Test
    void toModelTest() {
        //when
        Car car = carMapper.toModel(carRequest);

        //then
        assertEquals(carModel.getModel(), car.getModel());
        assertEquals(carModel.getBrand(), car.getBrand());
        assertEquals(carModel.getYear(), car.getYear());
    }

    @Test
    void toResponseTest() {
        //given
        when(brandMapper.toResponse(carModel.getBrand())).thenReturn(brandResponse);

        //when
        CarResponse car = carMapper.toResponse(carModel);

        //then
        assertEquals(carResponse.getId(), car.getId());
        assertEquals(carResponse.getModel(), car.getModel());
        assertEquals(carResponse.getBrand(), car.getBrand());
        assertEquals(carResponse.getYear(), car.getYear());
    }
}