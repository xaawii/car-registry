package com.xmartin.carregistry.service.converters;

import com.xmartin.carregistry.domain.Brand;
import com.xmartin.carregistry.entity.BrandEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BrandConverterTest {

    @InjectMocks
    private BrandConverter brandConverter;

    BrandEntity brandEntity;
    Brand brandModel;

    @BeforeEach
    void setUp() {
        brandEntity = BrandEntity.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).cars(new ArrayList<>()).build();

        brandModel = Brand.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).build();
    }

    @Test
    void toBrandTest() {

        //when
        Brand brand = brandConverter.toBrand(brandEntity);

        //then
        assertEquals(brandModel.getId(), brand.getId());
        assertEquals(brandModel.getName(), brand.getName());
        assertEquals(brandModel.getCountry(), brand.getCountry());
        assertEquals(brandModel.getWarranty(), brand.getWarranty());
    }

    @Test
    void toEntityTest() {

        //when
        BrandEntity brand = brandConverter.toEntity(brandModel);

        //then
        assertEquals(brandEntity.getId(), brand.getId());
        assertEquals(brandEntity.getName(), brand.getName());
        assertEquals(brandEntity.getCountry(), brand.getCountry());
        assertEquals(brandEntity.getWarranty(), brand.getWarranty());
    }
}