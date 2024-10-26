package com.example.demo.repository.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document( collection = "products" )
@Data
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    private String productID;
    private String name;
    private BigDecimal price;
}
