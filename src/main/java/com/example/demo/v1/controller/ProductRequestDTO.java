package com.example.demo.v1.controller;


import com.example.demo.repository.entity.ProductEntity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

   @NotBlank( message = "name: blank name" )
   private String name;
   @NotNull( message = "price: blank price" )
   @Min( value = 0, message = "price: negative number" )
   private Double price;


    public ProductEntity toEntity( String id ) {
       return  ProductEntity.builder()
                .productID( id )
                .name( getName() )
                .price( getPrice() )
                .build();
    }

    public ProductEntity toEntity() {
        return  ProductEntity.builder()
                .productID( UUID.randomUUID().toString() )
                .name( getName() )
                .price( getPrice() )
                .build();
    }
}
