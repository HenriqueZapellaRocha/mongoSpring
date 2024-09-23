package com.example.demo.service.irepositories;

import com.example.demo.repository.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

//TODO: Esta interface simula o que já faz o ProductRepository
//TODO: para adicionar mais camadas de separação, pode se fazer uma facade
public interface IProductrepository {

    ProductEntity addProduct(ProductEntity productEntity);

    Optional<ProductEntity> getProduct(String id);

    public ProductEntity updateAll(ProductEntity productEntity);

    void deleteAllProductsById(List<String> productsId);

    List<ProductEntity> getAllProduct();

    Boolean verifyExistById(String id);

}
