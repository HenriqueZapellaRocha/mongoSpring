package com.example.demo.repository;

import java.security.ProtectionDomain;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Product;
import com.example.demo.service.irepositories.IProductrepository;

@Repository
public class ProductRepImp implements IProductrepository {

    private final ProductRepository productRepository;

    @Autowired
    public ProductRepImp(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product addProduct(final Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProduct(final String id) {
        return productRepository.findById(id);
    }

    @Override
    public Product updateAll(final Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteAllProductsById(final List<String> productsId) {
        productRepository.deleteAllById(productsId);
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public Boolean verifyExistById(final String id) {
        return (productRepository.existsById(id));
    }

}
