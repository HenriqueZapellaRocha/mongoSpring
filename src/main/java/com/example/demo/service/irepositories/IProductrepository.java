package com.example.demo.service.irepositories;

import com.example.demo.model.Product;

import java.util.List;
import java.util.Optional;

public interface IProductrepository {

    Product addProduct(Product product);

    Optional<Product> getProduct(String id);

    public Product updateAll(Product product);

    void deleteAllProductsById(List<String> productsId);

    List<Product> getAllProduct();

    Boolean verifyExistById(String id);

}
