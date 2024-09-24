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
@NoArgsConstructor
@Builder
public class ProductRequestDTO {

   @NotBlank(message = "Name: Blank name")
   private String name;
   @NotNull(message = "Price: Price is blank")
   @Min(value = 0, message = "Price: Negative number")
   private Integer price;
    

    public ProductEntity toEntity(String id) {
       return  ProductEntity.builder()
                .productID(id != null ? id : UUID.randomUUID().toString())
                .name(getName())
                .price(getPrice())
                .build();

    }
}
