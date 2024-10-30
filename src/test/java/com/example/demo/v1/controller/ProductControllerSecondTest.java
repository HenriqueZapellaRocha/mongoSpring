package com.example.demo.v1.controller;

import com.example.demo.dtos.InvalidInputValuesExceptionDTO;
import com.example.demo.dtos.NotFoundExceptionDTO;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.ProductRepository;
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
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles( "test" )
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerSecondTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @MockBean
    private CookieService cookieService;
    @Autowired
    private ProductController productController;

    private ProductRequestDTO productRequest;
    private ProductRequestDTO productRequest2;
    private ProductEntity product1;
    private ProductEntity product2;
    ProductResponseDTO productResponse1;
    ProductResponseDTO productResponse2;

    @BeforeEach
    public void setup() {
        productRequest2 = ProductRequestDTO.builder()
                .name( "JVM" )
                .price( new BigDecimal( "350.50" ) )
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
        productRepository.deleteAll();
    }

    @Test
    void productControllerTestAddProductReturnOk() throws Exception {
        //when
        productRequest = ProductRequestDTO.builder()
                .name( "CLANG" )
                .price( new BigDecimal( "200.0" ) )
                .build();
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

        assertEquals( productResponseDTO.name(), productRequest.name() );
        assertNotEquals( productResponseDTO.price().value(), productRequest.price() );
        assertEquals( productResponseDTO.price().currency(), "USD" );
        assertNotEquals( Objects.requireNonNull(productRepository.
                        findById(productResponseDTO.productID()).orElse(null))
                .getPrice(), productRequest.price() );
        assertEquals( Objects.requireNonNull(productRepository.
                        findById(productResponseDTO.productID()).orElse(null))
                .getName(), productRequest.name() );

        //doing when it is USD beacuse the return need be the same value
        //when
        productRequest = ProductRequestDTO.builder()
                .name( "CLANG" )
                .price( new BigDecimal( "200.0" ) )
                .build();
        //perform
        response = mockMvc.perform( post( "/product/add" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content( new ObjectMapper().writeValueAsString( productRequest ) )
                        .param( "currency", "USD" ))
                .andExpect(status().isOk());
        //expect
        productResponseDTO = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                ProductResponseDTO.class );

        assertEquals( productResponseDTO.name(), productRequest.name() );
        assertEquals( productResponseDTO.price().value(), productRequest.price() );
        assertEquals( productResponseDTO.price().currency(), "USD" );
        assertEquals(0, Objects.requireNonNull(productRepository.
                        findById(productResponseDTO.productID()).orElse(null))
                .getPrice().compareTo(productRequest.price()));
        assertEquals( Objects.requireNonNull(productRepository.
                        findById(productResponseDTO.productID()).orElse(null))
                .getName(), productRequest.name() );
    }


    @Test
    void productControllerTest_addInvalidProduct_trowExceptionAndReturnError() throws Exception {
        //when
        //blank name and negative number
        productRequest = ProductRequestDTO.builder()
                .name( "" )
                .price( new BigDecimal( "-200.0" ) )
                .build();

        //perform
        ResultActions response = mockMvc.perform( post( "/product/add" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( new ObjectMapper().writeValueAsString( productRequest ) )
                        .param( "currency", "BRL" ))
                .andExpect( status().isBadRequest() );;

        //expect
        response.andExpect( status().isBadRequest() );

        List<String> errors = new ObjectMapper().readValue( response.andReturn().getResponse().getContentAsString(),
                InvalidInputValuesExceptionDTO.class ).getErrors();

        assertTrue( errors.contains( "name: blank name" ) );
        assertTrue( errors.contains( "price: negative number" ) );

        //when, setting to null  the price
        ProductRequestDTO request2 = new ProductRequestDTO( productRequest.name(), null );

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
        productRequest = ProductRequestDTO.builder()
                .name( "CLANG" )
                .price( new BigDecimal( "200.0" ) )
                .build();
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
        productRequest = ProductRequestDTO.builder()
                .name( "CLANG" )
                .price( new BigDecimal( "200.0" ) )
                .build();

        ProductEntity productEntity = productRepository.save( productRequest.toEntity() );

        //perform
        ResultActions response = mockMvc.perform(get( "/product/"+productEntity.getProductID() )
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currency", "USD" ))
                .andExpect( status().isOk() );
        //expect
        ProductResponseDTO productResponseDTO = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                ProductResponseDTO.class );
        assertEquals( productResponseDTO.productID(), productEntity.getProductID() );
        assertEquals( productResponseDTO.name(), productEntity.getName() );
        assertEquals( productResponseDTO.price().value(), productEntity.getPrice() );
        assertEquals( productResponseDTO.price().currency(), "USD" );
    }

    @Test
    void productControllerTest_getProductById_throwProductNotFoundException() throws Exception {
        //when
        String randomIdNotExist = UUID.randomUUID().toString();
        //perform
        ResultActions response = mockMvc.perform(get( "/product/"+randomIdNotExist )
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
        productRequest = ProductRequestDTO.builder()
                .name( "CLANG" )
                .price( new BigDecimal( "200.0" ) )
                .build();

        ProductEntity productEntity = productRepository.save( productRequest.toEntity() );
        //perform
        ResultActions response = mockMvc.perform(get( "/product/"+productEntity.getProductID() )
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currency", "ZZZ" ))
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
        productRequest2 = ProductRequestDTO.builder()
                .name( "JVM" )
                .price( new BigDecimal( "350.50" ) )
                .build();
        productRequest = ProductRequestDTO.builder()
                .name( "CLANG" )
                .price( new BigDecimal( "200.0" ) )
                .build();
        productRepository.saveAll( List.of( productRequest.toEntity(), productRequest2.toEntity() ) );

        //perform
        ResultActions response = mockMvc.perform( get( "/product/All" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .param( "currency", "USD" ) )
                .andExpect( status().isOk() );

        //expect
        List<ProductResponseDTO> productsResponseList = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                new TypeReference<List<ProductResponseDTO>>(){} );

        assertEquals(productsResponseList.get(0).name(), productRequest.name() );
        assertEquals(productsResponseList.get(0).price().value(), productRequest.price() );
        assertEquals(productsResponseList.get(0).price().currency(), "USD" );
        assertEquals(productsResponseList.get(1).name(), productRequest2.name() );
        assertEquals(productsResponseList.get(1).price().value(), productRequest2.price() );
        assertEquals(productsResponseList.get(1).price().currency(), "USD" );
    }

    @Test
    void productControllerTest_getAllProducts_throwCurrencyNotFoundException() throws Exception {
        //when
        productRequest2 = ProductRequestDTO.builder()
                .name( "JVM" )
                .price( new BigDecimal( "350.50" ) )
                .build();
        productRequest = ProductRequestDTO.builder()
                .name( "CLANG" )
                .price( new BigDecimal( "200.0" ) )
                .build();
        productRepository.saveAll( List.of( productRequest.toEntity(), productRequest2.toEntity() ) );
        //perform
        ResultActions response = mockMvc.perform(get( "/product/All" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("currency", "DUMB CURRENCy" ))
                .andExpect( status().isNotFound() );
        //expect
        NotFoundExceptionDTO notFound = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                NotFoundExceptionDTO.class );
        assertEquals( notFound.getError(), "currency not found" );
    }

    @Test
    void productControllerTest_getAllProductsWithBlankDB_returnBlankList() throws Exception {
        //when, nothing ;)
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
        productRequest = ProductRequestDTO.builder()
                .name( "CLANG" )
                .price( new BigDecimal( "200.0" ) )
                .build();
        ProductEntity productEntity = productRepository.save( productRequest.toEntity() );
        //perform
        ResultActions response = mockMvc.perform(get("/product/last")
                        .param( "currency", "BRL" )
                        .cookie( new Cookie("last", productEntity.getProductID() ) )
                        .contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
        //expect
        ProductResponseDTO productResponseDTO = new ObjectMapper().readValue(
                response.andReturn().getResponse().getContentAsString(),
                ProductResponseDTO.class );
        assertEquals( productResponseDTO.name(), productEntity.getName() );
        assertNotEquals( productResponseDTO.price().value(), productRequest.price() );
        assertEquals( productResponseDTO.price().currency(), "BRL" );
        assertEquals( productResponseDTO.productID(), productEntity.getProductID() );
    }

    @Test
    void productControllerTest_getLast_returnErrorCookieNotSet() throws Exception {
        //when, nothing ;)
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
        productRequest2 = ProductRequestDTO.builder()
                .name( "JVM" )
                .price( new BigDecimal( "350.50" ) )
                .build();
        productRequest = ProductRequestDTO.builder()
                .name( "CLANG" )
                .price( new BigDecimal( "200.0" ) )
                .build();
        ProductEntity productEntity = productRepository.save( productRequest.toEntity() );
        ProductEntity productEntity2 =  productRepository.save( productRequest2.toEntity() );
        //perform
        ResultActions response = mockMvc.perform( delete( "/product" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( new ObjectMapper().writeValueAsString( List.of( productEntity.getProductID()
                        , productEntity2.getProductID() ) ) ) );
        //expect
        response.andExpect( status().isOk() );
        assertTrue( response.andReturn().getResponse().getContentAsString().isEmpty() );
    }

    @Test
    void productControllerTest_DeleteById_returnOK() throws Exception {
        //when
        productRequest = ProductRequestDTO.builder()
                .name( "JVM" )
                .price( new BigDecimal( "200.0" ) )
                .build();
        ProductEntity productEntity = productRepository.save( productRequest.toEntity() );
        //perform
        ResultActions response = mockMvc.perform( delete( "/product/"+productEntity.getProductID() ) );
        //expect
        response.andExpect( status().isOk() );
        assertTrue( response.andReturn().getResponse().getContentAsString().isEmpty() );
    }
}