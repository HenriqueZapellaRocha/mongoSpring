package com.example.demo.v1.controller;

import com.example.demo.repository.entity.ProductEntity;
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

    //TODO: Os 2 métodos abaixo podem ser convertidos em um só, com o id sendo opcional

    public ProductEntity createEntity() {
       return  ProductEntity.builder()
                .productID(UUID.randomUUID().toString())
                .name(getName())
                .price(getPrice())
                .build();

    }

    public ProductEntity toEntity(final String id) {
        return  ProductEntity.builder()
                .productID(id)
                .name(getName())
                .price(getPrice())
                .build();
    }
}
