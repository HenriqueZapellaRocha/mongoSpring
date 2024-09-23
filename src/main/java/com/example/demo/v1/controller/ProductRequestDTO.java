package com.example.demo.v1.controller;

import com.example.demo.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDTO {

    private String name;
    private Integer price;


    public Product createEntity() {
       return  Product.builder()
                .productID(UUID.randomUUID().toString())
                .name(getName())
                .price(getPrice())
                .build();

    }

    public Product toEntity(final String id) {
        return  Product.builder()
                .productID(id)
                .name(getName())
                .price(getPrice())
                .build();
    }
}
