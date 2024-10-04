package com.example.demo.service.services;

import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.v1.controller.ProductRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        assertEquals(product, productRepository.findById(product.getProductID()).orElse(null));
    }

    @Test
    @DisplayName("get by ID test")
    void getByIdTest() {
       ProductEntity product = ProductEntity.builder()
                                                    .productID(UUID.randomUUID().toString())
                                                    .name("Clang").price(200.0)
                                                    .build();

       when(exchangeService.makeExchange("USD", "BRL")).thenReturn(5.44);

       productRepository.save(product);
       ProductEntity productRecived = productService.getById(product.getProductID(),"USD", "BRL");

       assertNotNull(productRecived);
       assertNotNull(productRecived.getProductID());
       assertEquals(product.getProductID(), productRecived.getProductID());
       assertEquals(product.getName(), productRecived.getName());
       assertEquals(product.getPrice() * 5.44, productRecived.getPrice());
    }

    @Test
    @DisplayName("Update person")
    void updateTest() {
        ProductEntity product = ProductEntity.builder()
                                             .name("Clang")
                                             .productID(null)
                                             .price(200.0)
                                             .build();
        productRepository.save(product);
        ProductRequestDTO productRequestDTO = new ProductRequestDTO("GCC", 100.0);
        ProductEntity productUpdated = productService.update(productRequestDTO, product.getProductID());
        assertNotNull(productUpdated);
        assertEquals(productUpdated, productRepository.findById(product.getProductID()).orElse(null));
        assertNotEquals(product.getName(), productUpdated.getName());
        assertNotEquals(product.getPrice(), productUpdated.getPrice());
    }

    @Test
    @DisplayName("Get all products")
    void getAllTest() {
        LinkedList<ProductEntity> products = new LinkedList<>();
        products.add(ProductEntity.builder()
                .name("Clang")
                .productID(null)
                .price(200.0)
                .build());
        products.add(ProductEntity.builder()
                .name("GCC")
                .productID(null)
                .price(300.0)
                .build());
        products.add(ProductEntity.builder()
                .name("JVM")
                .productID(null)
                .price(500.0)
                .build());

        productRepository.saveAll(products);

        when(exchangeService.makeExchange(any(), any())).thenReturn(5.44);

        List<ProductEntity> list = productService.getAll("USD","BRL");
        assertNotNull(list);
        assertEquals(3, list.size());
    }

    @Test
    @DisplayName("Delete many products")
    void deleteTest() {
        LinkedList<ProductEntity> products = new LinkedList<>();
        products.add(ProductEntity.builder()
                .name("Clang")
                .productID(null)
                .price(200.0)
                .build());
        products.add(ProductEntity.builder()
                .name("GCC")
                .productID(null)
                .price(300.0)
                .build());
        products.add(ProductEntity.builder()
                .name("JVM")
                .productID(null)
                .price(500.0)
                .build());


        productRepository.saveAll(products);
        List<String> productsToDelete = productRepository.findAll().stream()
                .map(ProductEntity::getProductID)
                .toList();

       productService.deleteMany(productsToDelete);

       assertEquals(0,productRepository.findAll().size());
    }
}