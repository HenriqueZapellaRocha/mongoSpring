package com.example.demo.service.services;


import com.example.demo.exception.NotFoundException;
import com.example.demo.integration.exchange.ExchangeIntegration;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.v1.controller.ProductRequestDTO;
import com.example.demo.v1.controller.ProductResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles( "test" )
class ProductServiceTest {

    @MockBean
    private ExchangeIntegration exchangeIntegration;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;


    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void productServiceTest_addProduct_ReturnTheEntityCreated() {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO("Clang", new BigDecimal("200.00"));

        BigDecimal exchangeRate = new BigDecimal( "0.1824" );

        when( exchangeIntegration.makeExchange( "BRL", "USD" ) ).thenReturn( 0.1824 );

        ProductResponseDTO product = productService.add( productRequestDTO, "BRL", "USD" );

        assertNotNull(product);
        assertNotNull(product.productID());

        BigDecimal expectedPrice = productRequestDTO.price().multiply( exchangeRate );

        assertEquals( 0, product.price().value().compareTo( expectedPrice ) );
        assertEquals( "USD", product.price().currency() );
        assertEquals( productRequestDTO.name(), product.name() );
        assertNotNull( productRepository.findById( product.productID() ).orElse( null ) );
    }


    @Test
    void productServiceTest_getById_ReturnTheEntityById() {
       ProductEntity product = ProductEntity.builder()
                                                    .productID( UUID.randomUUID().toString() )
                                                    .name( "Clang" ).price(BigDecimal.valueOf(200.0))
                                                    .build();

       when( exchangeIntegration.makeExchange( any(), any() ) ).thenReturn( 5.44 );

       productRepository.save( product );
       ProductResponseDTO productRecived = productService.getById( product.getProductID(), "USD", "BRL" );

       assertNotNull( productRecived );
       assertNotNull( productRecived.productID() );
       assertEquals( product.getProductID(), productRecived.productID() );
       assertEquals( product.getName(), productRecived.name() );
       assertEquals( 0, product.getPrice().multiply(BigDecimal.valueOf(5.44))
                                                    .compareTo(productRecived.price().value() ) );
    }

    @Test
    void productServiceTest_getById_ThrowNotFoundException() {

        assertThrows( NotFoundException.class, () -> {
            productService.getById( "1231312312312", "USD", "BRL" );
        });
    }

    @Test
    void productServiceTest_updateProduct_UpdateCorrectlyAndReturnResponseDTO() {
        ProductEntity product = ProductEntity.builder()
                                             .name( "Clang" )
                                             .productID( null )
                                             .price( new BigDecimal("200.0") )
                                             .build();
        productRepository.save( product );
        ProductRequestDTO productRequestDTO = new ProductRequestDTO( "GCC", new BigDecimal(200.0) );
        ProductResponseDTO productUpdated = productService.update( productRequestDTO, product.getProductID() );
        assertNotNull( productUpdated );
        assertEquals( productUpdated.productID(), productRepository.findById( product.getProductID() )
                                                                .orElse( null ).getProductID() );

        assertNotEquals( product.getName(), productUpdated.name() );
        assertNotEquals( product.getPrice(), productRepository.findById( product.getProductID())
                                                                .orElse( null ).getPrice() );
    }

    @Test
    public void productServiceTest_UpdateProduct_ThrowNotFoundException() {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO("he", BigDecimal.valueOf(200.0));
        String productId = "123";

        assertThrows(
                NotFoundException.class,
                () -> productService.update(productRequestDTO, productId)
        );
    }

    @Test
    void productServiceTest_GetAllProducts_ReturnAllProductsEntitiesInDb() {
        LinkedList<ProductEntity> products = new LinkedList<>();
        products.add( ProductEntity.builder()
                .name( "Clang" )
                .productID( null )
                .price( BigDecimal.valueOf( 200.0 ) )
                .build() );
        products.add( ProductEntity.builder()
                .name( "GCC" )
                .productID( null )
                .price( BigDecimal.valueOf( 300.0 ) )
                .build() );
        products.add( ProductEntity.builder()
                .name( "JVM" )
                .productID( null )
                .price( BigDecimal.valueOf( 500.0 ) )
                .build() );

        productRepository.saveAll( products );

        when( exchangeIntegration.makeExchange( any(), any() ) ).thenReturn( 5.44 );

        List<ProductResponseDTO> list = productService.getAll( "USD", "BRL" );
        assertNotNull( list );
        assertEquals( 3, list.size() );
    }

    @Test
    void productServiceTest_deleteProducts_ReturnVoid() {
        LinkedList<ProductEntity> products = new LinkedList<>();
        products.add( ProductEntity.builder()
                .name( "Clang" )
                .productID( null )
                .price( new BigDecimal(200.0) )
                .build() );
        products.add( ProductEntity.builder()
                .name( "GCC" )
                .productID( null )
                .price( new BigDecimal( 300.0 ) )
                .build() );
        products.add( ProductEntity.builder()
                .name( "JVM" )
                .productID( null )
                .price( new BigDecimal( 500.0 ) )
                .build() );


        productRepository.saveAll( products );
        List<String> productsToDelete = productRepository.findAll().stream()
                .map( ProductEntity::getProductID )
                .toList();

       productService.deleteMany( productsToDelete );

       assertEquals( 0, productRepository.findAll().size() );
    }
}