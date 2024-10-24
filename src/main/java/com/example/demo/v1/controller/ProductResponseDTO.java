package com.example.demo.v1.controller;


import com.example.demo.repository.entity.ProductEntity;
import lombok.Builder;

import java.math.BigDecimal;


@Builder
public record ProductResponseDTO( String productID, String name, PriceResponse price  ) {


    public static ProductResponseDTO entityToResponse(ProductEntity productEntity, String currency) {

        return  ProductResponseDTO.builder().productID( productEntity.getProductID() )
                                            .name( productEntity.getName() )
                                            .price( new PriceResponse( currency, productEntity.getPrice() ) )
                                            .build();

    }

    public record PriceResponse( String currency, BigDecimal value ) {}
}
