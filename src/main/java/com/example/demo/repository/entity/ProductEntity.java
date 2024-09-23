package com.example.demo.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    //TODO: Esta é a entidade do banco de dados, renomeada para ProductEntity e movida para dentro do pacote repository

    @Id
    private String productID;
    private String name;
    private Integer price;
}
