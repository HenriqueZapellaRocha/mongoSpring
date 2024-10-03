package com.example.demo.service.services;

import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.v1.controller.ProductRequestDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceTest {

    @MockBean
    private ExchangeService exchangeService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;


    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("ProductService Add method test")
    void testProductServiceAdd() {
        //creating productRequest
        ProductRequestDTO productRequestDTO = new ProductRequestDTO("Clang", 200.0);
        //making a mock with mockito. We are testing just productService not the others.
        when(exchangeService.makeExchange("BRL", "USD")).thenReturn(0.1824);

        ProductEntity product = productService.add(productRequestDTO,"BRL","USD");
        assertNotNull(product);
        assertNotNull(product.getProductID());
        assertEquals(200 * 0.1824, product.getPrice());
        assertEquals(productRequestDTO.getName(), product.getName());

    }

    @Test
    void getById() {
    }

    @Test
    void update() {
    }

    @Test
    void getAll() {
    }

    @Test
    void deleteMany() {
    }
}