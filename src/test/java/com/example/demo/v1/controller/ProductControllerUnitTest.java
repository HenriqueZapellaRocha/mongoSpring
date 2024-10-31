package com.example.demo.v1.controller;

import com.example.demo.dtos.InvalidInputValuesExceptionDTO;
import com.example.demo.dtos.NotFoundExceptionDTO;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.service.services.CookieService;
import com.example.demo.service.services.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles( "test" )
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;


    private ProductRequestDTO productRequest;
    private ProductEntity product1;
    private ProductEntity product2;
    ProductResponseDTO productResponse1;
    ProductResponseDTO productResponse2;

    @BeforeEach
    public void setup() {
        productRequest = ProductRequestDTO.builder()
                .name( "CLANG" )
                .price( new BigDecimal( "200.0" ) )
                .build();

        productResponse1 = ProductResponseDTO.builder()
                .productID("123")
                .price(ProductResponseDTO.PriceResponse.builder()
                        .currency("USD")
                        .value(new BigDecimal("200.00"))
                        .build()).name("JVM")
                .build();

        productResponse2 = ProductResponseDTO.builder()
                .productID("321")
                .price(ProductResponseDTO.PriceResponse.builder()
                        .currency("USD")
                        .value(new BigDecimal("5500.00"))
                        .build()).name("WHITEMANE")
                .build();

        product1 = productRequest.toEntity();
        product2 = new ProductEntity( UUID.randomUUID().toString(),"GCC", new BigDecimal( "300.50" ) );
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void productControllerTestAddProductReturnOk() throws Exception {

        //when
        when(productService.add(any(),any(),any())).thenReturn( ProductResponseDTO.entityToResponse( product1, "USD" ) ) ;
        //perform
        ResultActions response = mockMvc.perform( post( "/product/add" )
                .contentType(MediaType.APPLICATION_JSON)
                .content( new ObjectMapper().writeValueAsString( productRequest ) )
                .param( "currency", "BRL" ))
                .andExpect(status().isOk());
        //expect
        ProductResponseDTO productResponseDTO = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                ProductResponseDTO.class );

        assertEquals( productResponseDTO.name(), product1.getName() );
        assertEquals( productResponseDTO.price().value(), product1.getPrice() );
        assertEquals( productResponseDTO.productID(), product1.getProductID() );
    }


    @Test
    void productControllerTest_addInvalidProduct_trowExceptionAndReturnError() throws Exception {
        //when
                                                        //blank name and negative number
        ProductRequestDTO productRequestDTO = new ProductRequestDTO( "", BigDecimal.valueOf( -1.0 ) ) ;

        //perform
        ResultActions response = mockMvc.perform( post( "/product/add" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( new ObjectMapper().writeValueAsString( productRequestDTO ) )
                .param( "currency", "BRL" ))
                .andExpect( status().isBadRequest() );;

        //expect
        response.andExpect( status().isBadRequest() );

        List<String> errors = new ObjectMapper().readValue( response.andReturn().getResponse().getContentAsString(),
                                                                    InvalidInputValuesExceptionDTO.class ).getErrors();

        assertTrue( errors.contains( "name: blank name" ) );
        assertTrue( errors.contains( "price: negative number" ) );

        //when, setting to null  the price
        ProductRequestDTO request2 = new ProductRequestDTO( productRequestDTO.name(), null );

        //perform
        response = mockMvc.perform( post( "/product/add" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( new ObjectMapper().writeValueAsString( request2 ) )
                .param( "currency", "BRL" ) );
        //expect
        response.andExpect( status().isBadRequest() );

        errors = new ObjectMapper().readValue( response.andReturn().getResponse().getContentAsString(),
                                                    InvalidInputValuesExceptionDTO.class ).getErrors();
        assertTrue( errors.contains( "price: blank price" ) );

    }

    @Test
    void productControllerTest_addProductWithInvalidCurrency_trowExceptionAndReturnError() throws Exception {
        //when
        when(productService.add(any(),any(),any())).thenThrow( new NotFoundException( "currency not found" ) );
        //perform
        ResultActions response = mockMvc.perform( post( "/product/add" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( new ObjectMapper().writeValueAsString( productRequest ) )
                .param( "currency", "ZUD" ))
                .andExpect( status().isNotFound() );
        //expect
        NotFoundExceptionDTO notFound = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                NotFoundExceptionDTO.class );

        assertEquals( notFound.getError(), "currency not found" );

    }

    @Test
    void productControllerTest_getProductById_returnProduct() throws Exception {
        //when
        when( productService.getById( any(), any(), any()) )
                    .thenReturn( productResponse1 );

        //perform
        ResultActions response = mockMvc.perform(get( "/product/"+product1.getProductID() )
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currency", "BRL" ))
                .andExpect( status().isOk() );
        //expect
        ProductResponseDTO productResponseDTO = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                ProductResponseDTO.class );
        assertEquals( productResponseDTO, productResponse1 );
    }

    @Test
    void productControllerTest_getProductById_throwProductNotFoundException() throws Exception {
        //when
        when( productService.getById( any(), any(), any()) ).thenThrow( new NotFoundException( "No product found" ) );
        //perform
        ResultActions response = mockMvc.perform(get( "/product/"+product1.getProductID() )
                .contentType(MediaType.APPLICATION_JSON)
                .param("currency", "BRL" ))
                .andExpect( status().isNotFound() );
        //expect
        NotFoundExceptionDTO notFound = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                NotFoundExceptionDTO.class );
        assertEquals( notFound.getError(), "No product found" );
    }

    @Test
    void productControllerTest_getProductById_throwNotFoundCurrencyException() throws Exception {
        //when
        when( productService.getById( any(), any(), any()) ).thenThrow( new NotFoundException( "currency not found" ) );
        //perform
        ResultActions response = mockMvc.perform(get( "/product/"+product1.getProductID() )
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currency", "BRL" ))
                .andExpect( status().isNotFound() );
        //expect
        NotFoundExceptionDTO notFound = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                NotFoundExceptionDTO.class );
        assertEquals( notFound.getError(), "currency not found" );
    }

    @Test
    void productControllerTest_getAllProducts_returnAllProducts() throws Exception {
        //when
        List<ProductResponseDTO> productsResponse = List.of(productResponse1, productResponse2);
         when( productService.getAll( any(), any()) ).thenReturn( productsResponse );

        //perform
         ResultActions response = mockMvc.perform( get( "/product/All" )
                 .contentType( MediaType.APPLICATION_JSON )
                 .param( "currency", "BRL" ) )
                 .andExpect( status().isOk() );

        //expect
        List<ProductResponseDTO> productsResponseList = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                new TypeReference<List<ProductResponseDTO>>(){} );

        assertEquals(productsResponseList.get(0), productResponse1);
        assertEquals(productsResponseList.get(1), productResponse2);
    }

    @Test
    void productControllerTest_getAllProducts_throwProductNotFoundException() throws Exception {
        //when
        when( productService.getAll( any(), any() ) ).thenThrow( new NotFoundException( "currency not found" ) );
        //perform
        ResultActions response = mockMvc.perform(get( "/product/All" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currency", "BRL" ))
                .andExpect( status().isNotFound() );
        //expect
        NotFoundExceptionDTO notFound = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                NotFoundExceptionDTO.class );
        assertEquals( notFound.getError(), "currency not found" );
    }

    @Test
    void productControllerTest_getAllProductsWithBlankDB_returnBlankList() throws Exception {
        //when
        when(productService.getAll( any(), any() ) ).thenReturn( new LinkedList<>() );
        //perform
        ResultActions response = mockMvc.perform( get( "/product/All" )
                .contentType( MediaType.APPLICATION_JSON )
                .param( "currency", "BRL" ) )
                .andExpect( status().isOk() );

        //expect
        response.andExpect( jsonPath( "$.length()", CoreMatchers.is( ( 0 ) )));
    }

    @Test
    void productControllerTest_getLast_returnTheLastProductInCookie() throws Exception {
        //when
        when( productService.getById(any(), any(), any()) )
                .thenReturn( productResponse1 );
        //perform
       ResultActions response = mockMvc.perform(get("/product/last")
                .param( "currency", "BRL" )
                        .cookie(new Cookie("last", product1.getProductID()))
                .contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
        //expect
        ProductResponseDTO productResponseDTO = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                ProductResponseDTO.class );
        assertEquals( productResponseDTO, productResponse1 );
    }

    @Test
    void productControllerTest_getLast_returnErrorCookieNotSet() throws Exception {
        //when
        when( productService.getById(any(), any(), any()) )
                .thenReturn(ProductResponseDTO.entityToResponse(product1, "BRL"));
        //perform
        ResultActions response = mockMvc.perform(get("/product/last")
                        .param( "currency", "BRL" )
                        .contentType( MediaType.APPLICATION_JSON ) )
                .andExpect(status().isBadRequest());
        //expect
        NotFoundExceptionDTO notFound = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                NotFoundExceptionDTO.class );
        assertEquals( notFound.getError(), "No cookie is set" );
    }

    @Test
    void productControllerTest_DeleteManyById_returnOK() throws Exception {
        //when
        doNothing().when( productService ).deleteMany( any() );
        //perform
        ResultActions response = mockMvc.perform( delete( "/product" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( new ObjectMapper().writeValueAsString( List.of( product1.getProductID()
                                                                        , product2.getProductID() ) )));
        //expect
        response.andExpect( status().isOk() );
        assertTrue( response.andReturn().getResponse().getContentAsString().isEmpty() );
    }

    @Test
    void productControllerTest_DeleteById_returnOK() throws Exception {
        //when
        doNothing().when( productService ).deleteMany( any() );
        //perform
        ResultActions response = mockMvc.perform( delete( "/product/"+product1.getProductID() ) );
        //expect
        response.andExpect( status().isOk() );
        assertTrue( response.andReturn().getResponse().getContentAsString().isEmpty() );
    }
}