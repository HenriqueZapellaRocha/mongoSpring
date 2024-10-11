package com.example.demo.service.services;


import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.repository.ProductRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.demo.v1.controller.ProductRequestDTO;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ExchangeService exchangeService;

    public ProductEntity add( ProductRequestDTO product, String from, String to ) {
        //Converting productRequest to productEntity ( DB version )
        product.setPrice(  product.getPrice() * exchangeService.makeExchange( from, to )  );
        ProductEntity productEntityEntitie = product.toEntity();
        return productRepository.save( productEntityEntitie );
    }

    public ProductEntity getById( String id, String from, String to ) {
        ProductEntity product = productRepository.findById( id )
                                                 .orElseThrow( () -> new NotFoundException( "No product found" ) );

        product.setPrice(  product.getPrice() * exchangeService.makeExchange( from, to )  );
        return product;
    }

    public ProductEntity update( ProductRequestDTO product, String id ) {
        ProductEntity productEntity = product.toEntity( id );
        if( productRepository.existsById( id ) ) {
            return productRepository.save( productEntity );
        } else {
            throw new NotFoundException( "Not found" );
        }
    }

    public List<ProductEntity> getAll( String from, String to ) {
        Double value = exchangeService.makeExchange(  from, to  );
        return productRepository.findAll().stream()
                .peek(  p -> p.setPrice(  p.getPrice() * value  )  )
                .collect( Collectors.toList() );
    }

    public void deleteMany( List<String> ids ) {
        if ( ids.isEmpty() ) {
            throw new NotFoundException( "Blank list" );
        }
        productRepository.deleteAllById( ids );
    }
}
