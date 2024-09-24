package com.example.demo.service.services;


import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.repository.ProductRepository;

import java.util.List;
import java.util.Objects;

import com.example.demo.v1.controller.ProductRequestDTO;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductEntity add(ProductRequestDTO product) {
        //Converting productRequest to productEntity ( DB version )
        ProductEntity productEntityEntitie = product.toEntity(null);
        return productRepository.save(productEntityEntitie);
    }

    public ProductEntity getById(String id) {
        return productRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("No product found"));
    }

    public ProductEntity update(ProductRequestDTO product, String id) {
        ProductEntity productEntity = product.toEntity(id);
        if(productRepository.existsById(id)) {
        return productRepository.save(productEntity);
        } else {
            throw new NotFoundException("Not found");
        }
    }

    public List<ProductEntity> getAll() {
        return productRepository.findAll();
    }

    public void deleteMany(List<String> ids) {
    if (ids.stream().anyMatch(Objects::isNull) || ids.isEmpty()) {
        throw new NotFoundException("Not found products with list ids");
    }
        productRepository.deleteAllById(ids);
    }
}
