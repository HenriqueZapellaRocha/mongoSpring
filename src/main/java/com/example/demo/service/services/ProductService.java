package com.example.demo.service.services;

import com.example.demo.exception.MissingInputValuesException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Product;
import com.example.demo.service.irepositories.IProductrepository;

import java.util.List;




import com.example.demo.v1.controller.ProductRequestDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Primary
public class ProductService {

    private final IProductrepository productRepository;


    public Product addProduct(final ProductRequestDTO product) {
        //Converting productRequest to productEntity ( DB version )
        Product productEntitie = product.createEntity();
        verifyAllCorrectFields(productEntitie);
        return productRepository.addProduct(productEntitie);
    }

    public Product getById(final String id) {
        return productRepository.getProduct(id).orElseThrow(() -> new MissingInputValuesException("No product found"));
    }

    public Product updateProduct(final ProductRequestDTO product, final String id) {
        Product productEntitie = product.toEntity(id);
        verifyAllCorrectFields(productEntitie);
        return productRepository.updateAll(productEntitie);
    }

    public List<Product> getAll() {
        return productRepository.getAllProduct();
    }

    public void deleteProductAllById(final List<String> ids) {
        IdsExceptionListString(ids);
        productRepository.deleteAllProductsById(ids);
    }

    private void IdsException(final List<Product> product) {
        final boolean hasProductIdNull = product
                                        .stream()
                                        .anyMatch(p -> p.getProductID() == null);

        if (hasProductIdNull) {
            throw new MissingInputValuesException(
                    "Product fields are not valid"
            );
        }

        product
            .stream()
            .filter(p -> !productRepository.verifyExistById(p.getProductID()))
            .findFirst()
            .ifPresent(p -> {
                throw new MissingInputValuesException(
                    "Product fields are not valid"
                );
            });
    }

    private void IdsExceptionString(final String id) {
        if ( !productRepository.verifyExistById(id) )
            throw new NotFoundException("Not Found");
    }

    private void IdsExceptionListString(final List<String> ids) {

        ids.forEach(this::IdsExceptionString);
    }

    private void verifyAllCorrectFields(final Product product) {

        if ( product.getProductID() == null || product.getName() == null ||  product.getPrice() == null)
            throw new MissingInputValuesException("Product fields are not valid");

    }
}
