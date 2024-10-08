package com.example.demo.v1.controller;

import com.example.demo.dtos.InvalidInputValuesExceptionDTO;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.service.services.CookieService;
import com.example.demo.service.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles( "test" )
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CookieService cookieService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductController productController;

    private ProductRequestDTO productRequest;
    private ProductEntity product1;
    private ProductEntity product2;

    @BeforeEach
    public void setup() {
        productRequest = ProductRequestDTO.builder()
                                          .name( "CLANG" )
                                          .price( 200.0 )
                                          .build();

        product1 = productRequest.toEntity( null );
        product2 = new ProductEntity( UUID.randomUUID().toString(),"GCC", 300.0 );
        productRepository.deleteAll();
        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    void productControllerTest_addProduct_returnAddedOK() throws Exception {

        when(productService.add(any(),any(),any())).thenReturn(product1);

        ResultActions response = mockMvc.perform( post( "/product/add" )
                .contentType(MediaType.APPLICATION_JSON)
                .content( new ObjectMapper().writeValueAsString( productRequest ) )
                .param( "currency", "BRL" ));

        response.andExpect(status().isOk())
                .andExpect( jsonPath( "$.name", CoreMatchers.is( product1.getName() ) ))
                .andExpect( jsonPath( "$.price", CoreMatchers.is( product1.getPrice() ) ));
    }


    @Test
    void productControllerTest_addInvalidProduct_trowExceptionAndReturnError() throws Exception {
                                                        //blank name and negative number
        ProductRequestDTO productRequestDTO = new ProductRequestDTO("",-1.0);

        ResultActions response = mockMvc.perform( post( "/product/add" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( new ObjectMapper().writeValueAsString( productRequestDTO ) )
                .param( "currency", "BRL" ));



        response.andExpect( status().isBadRequest() );

        List<String> errors = new ObjectMapper().readValue( response.andReturn().getResponse().getContentAsString(),
                                                                    InvalidInputValuesExceptionDTO.class ).getErrors();

        assertTrue( errors.contains( "name: blank name" ) );
        assertTrue( errors.contains( "price: negative number" ) );

        //setting to null  the price
        productRequestDTO.setPrice( null );

        response = mockMvc.perform( post( "/product/add" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( new ObjectMapper().writeValueAsString( productRequestDTO ) )
                .param( "currency", "BRL" ));



        response.andExpect(status().isBadRequest());

        errors = new ObjectMapper().readValue( response.andReturn().getResponse().getContentAsString(),
                                                    InvalidInputValuesExceptionDTO.class ).getErrors();

        assertTrue( errors.contains( "price: blank price" ) );

    }


    @Test
    void productControllerTest_getProductById_returnProduct() throws Exception {
        when( productService.getById( any(), any(), any()) ).thenReturn( product1 );

        ResultActions response = mockMvc.perform(get( "/product/"+product1.getProductID() )
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currency", "BRL" ))
                .andExpect(status().isOk())
                .andExpect( jsonPath( "$.name", CoreMatchers.is( product1.getName() ) ))
                .andExpect( jsonPath( "$.price", CoreMatchers.is( product1.getPrice() ) ));
    }
//
//    @Test
//    void getAll() {
//    }
//
//    @Test
//    void updateAll() {
//    }
//
//    @Test
//    void deleteById() {
//    }
//
//    @Test
//    void deleteMany() {
//    }
}