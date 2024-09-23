package com.example.demo.v1.controller;

import com.example.demo.exception.MissingInputValuesException;
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
    

    public ProductEntity toEntity(String id) {
       return  ProductEntity.builder()
                .productID(id != null ? id : UUID.randomUUID().toString())
                .name(getName())
                .price(getPrice())
                .build();

    }

    public void validate() {
       if ( this.getName() == null ||  this.getPrice() == null )
           throw new MissingInputValuesException("Product fields are not valid");
   }
}
