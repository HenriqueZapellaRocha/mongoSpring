package com.example.demo.service.services;

import com.example.demo.exception.NotFoundException;
import com.example.demo.integration.exchange.ExchangeIntegration;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.v1.controller.ProductRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    }

    @Test
    void productServiceTest_addProduct_ReturnTheEntityCreated() {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO( "Clang", 200.0 );
        when( exchangeIntegration.makeExchange( "BRL", "USD" ) ).thenReturn( 0.1824 );

        ProductEntity product = productService.add( productRequestDTO, "BRL", "USD" );
        assertNotNull( product );
        assertNotNull( product.getProductID() );
        assertEquals( 200 * 0.1824, product.getPrice() );
        assertEquals( productRequestDTO.getName(), product.getName() );
        assertEquals( product, productRepository.findById( product.getProductID() ).orElse( null ) );
    }

    @Test
    void productServiceTest_getById_ReturnTheEntityById() {
       ProductEntity product = ProductEntity.builder()
                                                    .productID( UUID.randomUUID().toString() )
                                                    .name( "Clang" ).price( 200.0 )
                                                    .build();

       when( exchangeIntegration.makeExchange( "USD", "BRL" ) ).thenReturn( 5.44 );

       productRepository.save( product );
       ProductEntity productRecived = productService.getById( product.getProductID(), "USD", "BRL" );

       assertNotNull( productRecived );
       assertNotNull( productRecived.getProductID() );
       assertEquals( product.getProductID(), productRecived.getProductID() );
       assertEquals( product.getName(), productRecived.getName() );
       assertEquals( product.getPrice() * 5.44, productRecived.getPrice() );
    }

    @Test
    void productServiceTest_getById_ThrowsNotFoundException() {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO("he", 120.0);
        String productId = "123";

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productService.getById( productId, "", "" )
        );

        assertEquals("No product found", exception.getMessage());
    }

    @Test
    void productServiceTest_updateProduct_ReturnTheEntityUpdated() {
        ProductEntity product = ProductEntity.builder()
                                             .name( "Clang" )
                                             .productID( null )
                                             .price( 200.0 )
                                             .build();
        productRepository.save( product );
        ProductRequestDTO productRequestDTO = new ProductRequestDTO( "GCC", 100.0 );
        ProductEntity productUpdated = productService.update( productRequestDTO, product.getProductID() );
        assertNotNull( productUpdated );
        assertEquals( productUpdated, productRepository.findById( product.getProductID() ).orElse( null ) );
        assertNotEquals( product.getName(), productUpdated.getName() );
        assertNotEquals( product.getPrice(), productUpdated.getPrice() );
    }

    @Test
    public void productServiceTest_UpdateProduct_ThrowNotFoundException() {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO("he", 120.0);
        String productId = "123";

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productService.update(productRequestDTO, productId)
        );

        assertEquals("Not found", exception.getMessage());
    }

    @Test
    void productServiceTest_GetAllProducts_ReturnAllProductsEntitiesInDb() {
        LinkedList<ProductEntity> products = new LinkedList<>();
        products.add( ProductEntity.builder()
                .name( "Clang" )
                .productID( null )
                .price( 200.0 )
                .build() );
        products.add( ProductEntity.builder()
                .name( "GCC" )
                .productID( null )
                .price( 300.0 )
                .build() );
        products.add( ProductEntity.builder()
                .name( "JVM" )
                .productID( null )
                .price( 500.0 )
                .build() );

        productRepository.saveAll( products );

        when( exchangeIntegration.makeExchange( any(), any() ) ).thenReturn( 5.44 );

        List<ProductEntity> list = productService.getAll( "USD", "BRL" );
        assertNotNull( list );
        assertEquals( 3, list.size() );
    }

    @Test
    void productServiceTest_deleteProducts_ReturnVoid() {
        LinkedList<ProductEntity> products = new LinkedList<>();
        products.add( ProductEntity.builder()
                .name( "Clang" )
                .productID( null )
                .price( 200.0 )
                .build() );
        products.add( ProductEntity.builder()
                .name( "GCC" )
                .productID( null )
                .price( 300.0 )
                .build() );
        products.add( ProductEntity.builder()
                .name( "JVM" )
                .productID( null )
                .price( 500.0 )
                .build() );


        productRepository.saveAll( products );
        List<String> productsToDelete = productRepository.findAll().stream()
                .map( ProductEntity::getProductID )
                .toList();

       productService.deleteMany( productsToDelete );

       assertEquals( 0, productRepository.findAll().size() );
    }

    @Test
    void productServiceTest_deleteProducts_ThrowExceptionBlankList() {
        LinkedList<String> products = new LinkedList<>();

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productService.deleteMany( products )
        );

        assertEquals("Blank list", exception.getMessage());

    }
}