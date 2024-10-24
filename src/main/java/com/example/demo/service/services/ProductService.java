package com.example.demo.service.services;


import com.example.demo.exception.NotFoundException;
import com.example.demo.integration.exchange.ExchangeIntegration;
import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import com.example.demo.v1.controller.ProductRequestDTO;
import com.example.demo.v1.controller.ProductResponseDTO;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ExchangeIntegration exchangeIntegration;

    public ProductResponseDTO add( ProductRequestDTO product, String from, String to ) {
        //Converting productRequest to productEntity ( DB version )
        ProductEntity productEntityEntitie = product.toEntity();
        productEntityEntitie.setPrice( BigDecimal.valueOf(
                product.price().doubleValue() * exchangeIntegration.makeExchange( from, to ) )  );

        return ProductResponseDTO.entityToResponse( productRepository.save( productEntityEntitie ), "USD" );
    }

    public ProductResponseDTO getById( String id, String from, String to ) {
        ProductEntity product = productRepository.findById( id )
                                                 .orElseThrow( () -> new NotFoundException( "No product found" ) );

        product.setPrice(BigDecimal.valueOf(
                product.getPrice().doubleValue() * exchangeIntegration.makeExchange( from, to ) ) );
        return ProductResponseDTO.entityToResponse(product, to);
    }

    public ProductResponseDTO update( ProductRequestDTO product, String id ) {
        ProductEntity productEntity = product.toEntity( id );
        if( productRepository.existsById( id ) ) {
            return ProductResponseDTO.entityToResponse(productRepository.save( productEntity ), "USD");
        } else {
            throw new NotFoundException( "Not found" );
        }
    }

    public List<ProductResponseDTO> getAll( String from, String to ) {
        Double value = exchangeIntegration.makeExchange( from, to );
        return productRepository.findAll().stream()
                .peek( p -> p.setPrice( BigDecimal.valueOf( p.getPrice().doubleValue() * value  ) ) )
                .map( p -> ProductResponseDTO.entityToResponse( p, to ) )
                .collect( Collectors.toList() );
    }

    public void deleteMany( List<String> ids ) {
        productRepository.deleteAllById( ids );
    }
}
