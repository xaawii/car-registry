package com.xmartin.carregistry.service.converters;

import com.xmartin.carregistry.domain.Brand;
import com.xmartin.carregistry.domain.Car;
import com.xmartin.carregistry.entity.BrandEntity;
import com.xmartin.carregistry.entity.CarEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarConverterTest {

    @InjectMocks
    private CarConverter carConverter;

    @Mock
    private BrandConverter brandConverter;


    Car carModel;
    CarEntity carEntity;
    BrandEntity brandEntity;
    Brand brandModel;

    @BeforeEach
    void setUp() {
        brandEntity = BrandEntity.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).cars(new ArrayList<>()).build();

        brandModel = Brand.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).build();

        carModel = Car.builder().id(1).brand(brandModel).model("Corsa")
                .price(15000.0).year(2010).colour("Red").description("")
                .fuelType("Diesel").mileage(2000).numDoors(4).build();
        carEntity = CarEntity.builder().id(1).brand(brandEntity).model("Corsa")
                .price(15000.0).year(2010).colour("Red").description("")
                .fuelType("Diesel").mileage(2000).numDoors(4).build();
    }

    @Test
    void toCarTest() {
        //given
        when(brandConverter.toBrand(brandEntity)).thenReturn(brandModel);

        //when
        Car car = carConverter.toCar(carEntity);

        //then
        assertEquals(carModel.getId(), car.getId());
        assertEquals(carModel.getModel(), car.getModel());
        assertEquals(carModel.getBrand(), car.getBrand());
        assertEquals(carModel.getYear(), car.getYear());
    }

    @Test
    void toEntityTest() {

        //given
        when(brandConverter.toEntity(brandModel)).thenReturn(brandEntity);

        //when
        CarEntity car = carConverter.toEntity(carModel);

        //then
        assertEquals(carEntity.getId(), car.getId());
        assertEquals(carEntity.getModel(), car.getModel());
        assertEquals(carEntity.getBrand(), car.getBrand());
        assertEquals(carEntity.getYear(), car.getYear());
    }
}