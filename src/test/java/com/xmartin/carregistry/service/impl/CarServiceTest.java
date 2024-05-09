package com.xmartin.carregistry.service.impl;

import com.xmartin.carregistry.domain.Brand;
import com.xmartin.carregistry.domain.Car;
import com.xmartin.carregistry.entity.BrandEntity;
import com.xmartin.carregistry.entity.CarEntity;
import com.xmartin.carregistry.exceptions.CarNotFoundException;
import com.xmartin.carregistry.repository.BrandRepository;
import com.xmartin.carregistry.repository.CarRepository;
import com.xmartin.carregistry.service.converters.CarConverter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @InjectMocks
    private CarServiceImpl carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CarConverter carConverter;

    Car car;
    CarEntity carEntity;

    BrandEntity brandEntity;
    Brand brand;

    List<Car> carModelList;
    List<CarEntity> carEntityList;

    @BeforeEach
    void setUpObjects() {
        brandEntity = BrandEntity.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).cars(new ArrayList<>()).build();

        brand = Brand.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).build();

        car = Car.builder().id(1).brand(brand).model("Corsa")
                .price(15000.0).year(2010).colour("Red").description("")
                .fuelType("Diesel").mileage(2000).numDoors(4).build();
        carEntity = CarEntity.builder().id(1).brand(brandEntity).model("Corsa")
                .price(15000.0).year(2010).colour("Red").description("")
                .fuelType("Diesel").mileage(2000).numDoors(4).build();

        carModelList = List.of(car);
        carEntityList = List.of(carEntity);

    }


    @SneakyThrows
    @Test
    void add_car_test_success() {

        //given
        when(carConverter.toEntity(car)).thenReturn(carEntity);
        when(brandRepository.findByNameIgnoreCase(car.getBrand().getName())).thenReturn(Optional.ofNullable(brandEntity));
        when(carRepository.save(carEntity)).thenReturn(carEntity);
        when(carConverter.toCar(carEntity)).thenReturn(car);

        //when
        Car carResult = carService.addCar(car);

        //then
        assertEquals(car, carResult);

    }


    @Test
    void add_car_test_fails() {

        //given
        when(brandRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());

        //then
        assertThrows(Exception.class, () -> carService.addCar(car));

    }


    @SneakyThrows
    @Test
    void get_cars_test() {

        //given
        when(carRepository.findAll()).thenReturn(carEntityList);
        when(carConverter.toCarList(carEntityList)).thenReturn(carModelList);

        //then
        assertEquals(carModelList, carService.getCars(PageRequest.of(0, 10)).get());

    }

    @Test
    void getCarById_test() {
        //given
        when(carRepository.findById(1)).thenReturn(Optional.ofNullable(carEntity));
        when(carConverter.toCar(carEntity)).thenReturn(car);

        //then
        assertEquals(car, carService.getCarById(1));
    }


    @SneakyThrows
    @Test
    void updateCar_test() {
        //given
        Car newCar = Car.builder().id(1).brand(brand).model("Astra")
                .price(1500.0).year(2010).colour("Red").description("")
                .fuelType("Gas").mileage(2000).numDoors(5).build();
        CarEntity newCarEntity = CarEntity.builder().id(1).brand(brandEntity).model("Astra")
                .price(1500.0).year(2010).colour("Red").description("")
                .fuelType("Gas").mileage(2000).numDoors(5).build();

        when(carRepository.findById(1)).thenReturn(Optional.ofNullable(carEntity));
        when(brandRepository.findByNameIgnoreCase(car.getBrand().getName())).thenReturn(Optional.ofNullable(brandEntity));
        when(carConverter.toEntity(newCar)).thenReturn(newCarEntity);
        when(carRepository.save(any(CarEntity.class))).thenReturn(newCarEntity);
        when(carConverter.toCar(any(CarEntity.class))).thenReturn(newCar);

        //then
        assertEquals(newCar, carService.updateCar(newCar, 1));
    }


    @Test
    void deleteCar_test_fails() {
        //given
        when(carRepository.findById(1)).thenReturn(Optional.empty());

        //then
        assertThrows(CarNotFoundException.class, () -> carService.deleteCar(1));
    }

    @SneakyThrows
    @Test
    void addCars_test_success() {
        //given
        when(carConverter.toEntityList(carModelList)).thenReturn(carEntityList);
        when(brandRepository.findByNameIgnoreCase(car.getBrand().getName())).thenReturn(Optional.ofNullable(brandEntity));
        when(carConverter.toCarList(carEntityList)).thenReturn(carModelList);

        //then
        assertEquals(carModelList, carService.addCars(carModelList).get());
    }


    @Test
    void addCars_test_fails() {
        //given

        when(carConverter.toEntityList(carModelList)).thenReturn(carEntityList);
        when(brandRepository.findByNameIgnoreCase(car.getBrand().getName())).thenReturn(Optional.empty());

        //then
        assertThrows(Exception.class, () -> carService.addCars(carModelList));
    }

}
