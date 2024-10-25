package com.example.demo.v1.controller;

import com.example.demo.repository.entity.ProductEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProductRequestDTO(
        @Schema( description = "Name of the product", example = "JVM" )
        @NotBlank( message = "name: blank name" )
        String name,

        @Schema( description = "Price of the product", example = "200.50" )
        @NotNull( message = "price: blank price" )
        @Min( value = 0, message = "price: negative number" )
        BigDecimal price
) {

    public ProductEntity toEntity( String id ) {
        return ProductEntity.builder()
                .productID( id )
                .name( this.name )
                .price( this.price )
                .build();
    }

    public ProductEntity toEntity() {
        return ProductEntity.builder()
                .productID( UUID.randomUUID().toString() )
                .name( this.name )
                .price( this.price )
                .build();
    }
}
