package com.example.demo.v1.controller;

import com.example.demo.dtos.InvalidInputValuesExceptionDTO;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.service.services.CookieService;
import com.example.demo.service.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
                                          .price( new BigDecimal( "200.0" ) )
                                          .build();

        product1 = productRequest.toEntity();
        product2 = new ProductEntity( UUID.randomUUID().toString(),"GCC", new BigDecimal( "300.50" ) );
        productRepository.deleteAll();
        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    void productControllerTest_addProduct_returnAddedOK() throws Exception {

        when(productService.add(any(),any(),any())).thenReturn( ProductResponseDTO.entityToResponse( product1, "USD" ) ) ;

        ResultActions response = mockMvc.perform( post( "/product/add" )
                .contentType(MediaType.APPLICATION_JSON)
                .content( new ObjectMapper().writeValueAsString( productRequest ) )
                .param( "currency", "BRL" ));

        response.andExpect(status().isOk())
                .andExpect( jsonPath( "$.name", CoreMatchers.is( product1.getName() ) ))
                .andExpect( jsonPath( "$.price.value", CoreMatchers.is( 200.0 ) ))
                .andExpect( jsonPath( "$.price.currency", CoreMatchers.is( "USD" ) ));
    }


    @Test
    void productControllerTest_addInvalidProduct_trowExceptionAndReturnError() throws Exception {
                                                        //blank name and negative number
        ProductRequestDTO productRequestDTO = new ProductRequestDTO( "", BigDecimal.valueOf( -1.0 ) ) ;

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
        ProductRequestDTO request2 = new ProductRequestDTO( productRequestDTO.name(), null );

        response = mockMvc.perform( post( "/product/add" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( new ObjectMapper().writeValueAsString( request2 ) )
                .param( "currency", "BRL" ) );



        response.andExpect( status().isBadRequest() );

        errors = new ObjectMapper().readValue( response.andReturn().getResponse().getContentAsString(),
                                                    InvalidInputValuesExceptionDTO.class ).getErrors();

        assertTrue( errors.contains( "price: blank price" ) );

    }

    @Test
    void productControllerTest_addProductWithInvalidCurrency_trowExceptionAndReturnError() throws Exception {

        when(productService.add(any(),any(),any())).thenThrow( new NotFoundException( "currency not found" ) );

        ResultActions response = mockMvc.perform( post( "/product/add" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( new ObjectMapper().writeValueAsString( productRequest ) )
                .param( "currency", "ZUD" ))
                .andExpect( status().isNotFound() )
                .andExpect( jsonPath( "$.error", CoreMatchers.is( "currency not found" ) ) );

    }


    @Test
    void productControllerTest_getProductById_returnProduct() throws Exception {
        when( productService.getById( any(), any(), any()) )
                    .thenReturn( ProductResponseDTO.entityToResponse( product1, "BRL" ) );

        ResultActions response = mockMvc.perform(get( "/product/"+product1.getProductID() )
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currency", "BRL" ))
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.name", CoreMatchers.is( product1.getName() ) ))
                .andExpect( jsonPath( "$.price.value", CoreMatchers.is( product1.getPrice().doubleValue() ) ))
                .andExpect( jsonPath( "$.price.currency", CoreMatchers.is( "BRL" ) ));
    }

    @Test
    void productControllerTest_getProductById_throwProductNotFoundException() throws Exception {

        when( productService.getById( any(), any(), any()) ).thenThrow( new NotFoundException( "No product found" ) );

        ResultActions response = mockMvc.perform(get( "/product/"+product1.getProductID() )
                .contentType(MediaType.APPLICATION_JSON)
                .param("currency", "BRL" ))
                .andExpect( status().isNotFound() )
                .andExpect( jsonPath( "$.error", CoreMatchers.is( "No product found" ) ) );
    }

    @Test
    void productControllerTest_getProductById_throwNotFoundCurrencyException() throws Exception {

        when( productService.getById( any(), any(), any()) ).thenThrow( new NotFoundException( "currency not found" ) );

        ResultActions response = mockMvc.perform(get( "/product/"+product1.getProductID() )
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currency", "BRL" ))
                .andExpect( status().isNotFound() )
                .andExpect( jsonPath( "$.error", CoreMatchers.is( "currency not found" ) ) );
    }

    @Test
    void productControllerTest_getAllProducts_returnAllProducts() throws Exception {

        List<ProductResponseDTO> productsResponse = productRepository.findAll()
                .stream()
                .map(p -> ProductResponseDTO.entityToResponse(p, "BRL"))
                .toList();

         when( productService.getAll( any(), any()) ).thenReturn( productsResponse );

         ResultActions response = mockMvc.perform( get( "/product/All" )
                 .contentType( MediaType.APPLICATION_JSON )
                 .param( "currency", "BRL" ) );


        response.andExpect(status().isOk())
                .andExpect( jsonPath( "$[0].name", CoreMatchers.is( ( product1.getName() ) )))
                .andExpect( jsonPath( "$[0].price.value", CoreMatchers.is( ( product1.getPrice().doubleValue() ) )))
                .andExpect( jsonPath( "$[0].productID", CoreMatchers.is( ( product1.getProductID() ) )))
                .andExpect( jsonPath( "$[0].price.currency", CoreMatchers.is( "BRL" ) ))
                .andExpect( jsonPath( "$[1].productID", CoreMatchers.is( ( product2.getProductID() ) )))
                .andExpect( jsonPath( "$[1].name", CoreMatchers.is( ( product2.getName() ) )))
                .andExpect( jsonPath( "$[1].price.value", CoreMatchers.is( ( product2.getPrice().doubleValue() ) )))
                .andExpect( jsonPath( "$[1].price.currency", CoreMatchers.is( "BRL" ) ));
    }

    @Test
    void productControllerTest_getAllProducts_throwProductNotFoundException() throws Exception {

        when( productService.getAll( any(), any() ) ).thenThrow( new NotFoundException( "currency not found" ) );

        ResultActions response = mockMvc.perform(get( "/product/All" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currency", "BRL" ))
                .andExpect( status().isNotFound() )
                .andExpect( jsonPath( "$.error", CoreMatchers.is( "currency not found" ) ) );
    }

    @Test
    void productControllerTest_getAllProductsWithBlankDB_returnBlankList() throws Exception {
        when(productService.getAll( any(), any() ) ).thenReturn( new LinkedList<>() );

        ResultActions response = mockMvc.perform( get( "/product/All" )
                .contentType( MediaType.APPLICATION_JSON )
                .param( "currency", "BRL" ) );


        response.andExpect( status().isOk() )
                .andExpect( jsonPath( "$.length()", CoreMatchers.is( ( 0 ) )));
    }

    @Test
    void productControllerTest_DeleteManyById_returnOK() throws Exception {
        doNothing().when( productService ).deleteMany( any() );
        ResultActions response = mockMvc.perform( delete( "/product" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( new ObjectMapper().writeValueAsString( List.of( product1.getProductID()
                                                                        ,product2.getProductID() ) )));

        response.andExpect( status().isOk() );
        assertTrue( response.andReturn().getResponse().getContentAsString().isEmpty() );
    }

    @Test
    void productControllerTest_DeleteById_returnOK() throws Exception {
        doNothing().when( productService ).deleteMany( any() );

        ResultActions response = mockMvc.perform( delete( "/product/"+product1.getProductID() ) );
        response.andExpect( status().isOk() );
        assertTrue( response.andReturn().getResponse().getContentAsString().isEmpty() );
    }
}