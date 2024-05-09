package com.xmartin.carregistry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmartin.carregistry.domain.Brand;
import com.xmartin.carregistry.filter.JwtAuthenticationFilter;
import com.xmartin.carregistry.controller.dtos.BrandRequest;
import com.xmartin.carregistry.controller.dtos.BrandResponse;
import com.xmartin.carregistry.controller.mappers.BrandMapper;
import com.xmartin.carregistry.service.impl.BrandServiceImpl;
import com.xmartin.carregistry.service.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BrandController.class)
//@AutoConfigureMockMvc(addFilters = false)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BrandServiceImpl brandService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter authenticationFilter;

    @MockBean
    private BrandMapper brandMapper;

    Brand brandModel;
    BrandResponse brandResponse;
    BrandRequest brandRequest;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        brandModel = Brand.builder().id(1).name("Opel")
                .country("Deutchland").warranty(2).build();
        brandResponse = new BrandResponse(1, "Opel", 2, "Deutchland");
        brandRequest = new BrandRequest("Opel", 2, "Deutchland");
    }


    @Test
    @WithMockUser(username = "xavi@test.com", roles = "USER")
    void getBrandByIdTest_valid() throws Exception {
        //given
        when(brandService.getBrandById(1)).thenReturn(brandModel);
        when(brandMapper.toResponse(any(Brand.class))).thenReturn(brandResponse);


        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/brands/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Opel"));
    }

    @Test
    @WithMockUser(username = "xavi@test.com", roles = "USER")
    void getBrandsTest_valid() throws Exception {
        //given
        List<Brand> brandList = List.of(brandModel);
        when(brandService.getBrands()).thenReturn(CompletableFuture.completedFuture(brandList));
        when(brandMapper.toResponseList(brandList)).thenReturn(List.of(brandResponse));


        //when - then
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Opel"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void deleteBrandTest_valid() throws Exception {
        //given

        when(brandMapper.toResponse(any(Brand.class))).thenReturn(brandResponse);


        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/brands/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted brand with id 1"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void addBrandTest_valid() throws Exception {

        //given
        when(brandService.addBrand(any(Brand.class))).thenReturn(brandModel);
        when(brandMapper.toModel(any(BrandRequest.class))).thenReturn(brandModel);
        when(brandMapper.toResponse(any(Brand.class))).thenReturn(brandResponse);

        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(brandRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Opel"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void updateBrandTest_valid() throws Exception {

        //given
        Brand brandModelUpdated = Brand.builder().id(1).name("Opel")
                .country("Japan").warranty(2).build();
        BrandResponse brandResponseUpdated = new BrandResponse(1, "Opel", 2, "Japan");

        when(brandMapper.toModel(any(BrandRequest.class))).thenReturn(brandModelUpdated);
        when(brandService.updateBrand(brandModelUpdated, 1)).thenReturn(brandModelUpdated);
        when(brandMapper.toResponse(any(Brand.class))).thenReturn(brandResponseUpdated);

        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/brands/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(brandRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Opel"))
                .andExpect(jsonPath("$.country").value("Japan"));
    }
}