package com.xmartin.carregistry.controller.mappers;

import com.xmartin.carregistry.domain.Brand;
import com.xmartin.carregistry.controller.dtos.BrandRequest;
import com.xmartin.carregistry.controller.dtos.BrandResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BrandMapperTest {

    @InjectMocks
    private BrandMapper brandMapper;

    Brand brandModel;
    BrandResponse brandResponse;
    BrandRequest brandRequest;

    @BeforeEach
    void setUp() {
        brandModel = Brand.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).build();
        brandResponse = new BrandResponse(1, "Opel", 2, "Deutchland");
        brandRequest = new BrandRequest("Opel", 2, "Deutchland");
    }

    @Test
    void toModelTest() {

        //when
        Brand brand = brandMapper.toModel(brandRequest);
        brand.setId(1);

        //then
        assertEquals(brandModel.getId(), brand.getId());
        assertEquals(brandModel.getName(), brand.getName());
        assertEquals(brandModel.getCountry(), brand.getCountry());
        assertEquals(brandModel.getWarranty(), brand.getWarranty());
    }

    @Test
    void toResponseTest() {

        //when
        BrandResponse brand = brandMapper.toResponse(brandModel);

        //then
        assertEquals(brand.getId(), brandModel.getId());
        assertEquals(brand.getName(), brandModel.getName());
        assertEquals(brand.getCountry(), brandModel.getCountry());
        assertEquals(brand.getWarranty(), brandModel.getWarranty());
    }
}