package com.xmartin.carregistry.service.impl;

import com.xmartin.carregistry.domain.Brand;
import com.xmartin.carregistry.entity.BrandEntity;
import com.xmartin.carregistry.exceptions.BrandConflictException;
import com.xmartin.carregistry.exceptions.BrandNotFoundException;
import com.xmartin.carregistry.repository.BrandRepository;
import com.xmartin.carregistry.service.converters.BrandConverter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {
    @InjectMocks
    private BrandServiceImpl brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BrandConverter brandConverter;

    BrandEntity brandEntity;
    Brand brandExpected;

    @BeforeEach
    void setObjects() {
        brandEntity = BrandEntity.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).cars(new ArrayList<>()).build();

        brandExpected = Brand.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).build();
    }

    @SneakyThrows
    @Test
    void get_brand_by_id_test() {

        //given
        when(brandRepository.findById(1)).thenReturn(Optional.ofNullable(brandEntity));
        when(brandConverter.toBrand(brandEntity)).thenReturn(brandExpected);

        //when
        Brand brand = brandService.getBrandById(1);

        //then
        assertEquals(brandExpected, brand);

    }

    @Test
    void get_brands_test() throws ExecutionException, InterruptedException {

        //given
        List<BrandEntity> brandEntityList = List.of(brandEntity);

        Brand brand = Brand.builder().id(1).name("Opel").country("Deutchland").warranty(2).build();
        List<Brand> brandModelList = List.of(brand);


        when(brandRepository.findAll()).thenReturn(brandEntityList);
        when(brandConverter.toBrandList(brandEntityList)).thenReturn(brandModelList);


        //when
        CompletableFuture<List<Brand>> brandResultList = brandService.getBrands();


        //then
        assertEquals(brandModelList, brandResultList.get());

    }

    @SneakyThrows
    @Test
    void update_brand_test() {

        //given
        Brand brandModelNew = Brand.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).build();

        BrandEntity brandEntityNew = BrandEntity.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).cars(new ArrayList<>()).build();

        when(brandRepository.findById(1)).thenReturn(Optional.ofNullable(brandEntity));
        when(brandConverter.toEntity(brandModelNew)).thenReturn(brandEntityNew);
        when(brandRepository.save(brandEntityNew)).thenReturn(brandEntityNew);
        when(brandConverter.toBrand(brandEntityNew)).thenReturn(brandModelNew);

        //when
        Brand updatedBrand = brandService.updateBrand(brandModelNew, 1);

        //then
        assertEquals(brandModelNew, updatedBrand);
    }


    @Test
    void delete_brand_test_fails() {

        //given
        when(brandRepository.findById(1)).thenReturn(Optional.empty());

        //then
        assertThrows(BrandNotFoundException.class, () -> brandService.deleteBrand(1));

    }

    @SneakyThrows
    @Test
    void add_brand_test() {

        //given
        when(brandRepository.findByNameIgnoreCase(brandExpected.getName())).thenReturn(Optional.empty());
        when(brandConverter.toEntity(brandExpected)).thenReturn(brandEntity);
        when(brandRepository.save(brandEntity)).thenReturn(brandEntity);
        when(brandConverter.toBrand(brandEntity)).thenReturn(brandExpected);

        //then
        assertEquals(brandExpected, brandService.addBrand(brandExpected));

    }

    @Test
    void add_brand_test_fails() {

        //given
        when(brandRepository.findByNameIgnoreCase(brandExpected.getName())).thenReturn(Optional.ofNullable(brandEntity));

        //then
        assertThrows(BrandConflictException.class, () -> brandService.addBrand(brandExpected));

    }
}
