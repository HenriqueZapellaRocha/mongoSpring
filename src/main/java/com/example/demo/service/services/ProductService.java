package com.example.demo.service.services;

import com.example.demo.exception.MissingInputValuesException;
import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.repository.ProductRepository;

import java.util.List;




import com.example.demo.v1.controller.ProductRequestDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
//TODO: como só existe este Bean do tipo ProductService, não tem necessidade de marcar como Primary
@Primary
public class ProductService {

//    TODO: O próprio repository criado pelo spring já se encarrega das operações básicas de CRUD,
//    TODO: não tendo necessidade de implementar novamente
    private final ProductRepository productRepository;

//  TODO: as assinaturas dos métodos não tem necessidade de serem final
//    TODO: Já que estamos dentro do ProductService, não precisa repetir "product" nos métodos: ex: add() ou create()
    public ProductEntity addProduct(final ProductRequestDTO product) {
        //Converting productRequest to productEntity ( DB version )
        ProductEntity productEntityEntitie = product.createEntity();
        return productRepository.save(productEntityEntitie);
    }

    public ProductEntity getById(final String id) {
        return productRepository.findById(id).orElseThrow(() -> new MissingInputValuesException("No product found"));
    }

    //    TODO: Já que estamos dentro do ProductService, não precisa repetir "product" nos métodos: ex: update()
    public ProductEntity updateProduct(final ProductRequestDTO product, final String id) {
        ProductEntity productEntity = product.toEntity(id);
        return productRepository.save(productEntity);
    }

    public List<ProductEntity> getAll() {
        return productRepository.findAll();
    }

    //    TODO: Já que estamos dentro do ProductService, não precisa repetir "product" nos métodos: ex: delete()
//    TODO: Não está deletando todos (all) e sim vários (many). Ex: deleteMany()
    public void deleteProductAllById(final List<String> ids) {
        productRepository.deleteAllById(ids);
    }
//  Todo: Método não utilizado

//    private void IdsException(final List<Product> product) {
//        final boolean hasProductIdNull = product
//                                        .stream()
//                                        .anyMatch(p -> p.getProductID() == null);
//
//        if (hasProductIdNull) {
//            throw new MissingInputValuesException(
//                    "Product fields are not valid"
//            );
//        }
//
//        product
//            .stream()
//            .filter(p -> !productRepository.findById(p.getProductID()))
//            .findFirst()
//            .ifPresent(p -> {
//                throw new MissingInputValuesException(
//                    "Product fields are not valid"
//                );
//            });
//    }

//    TODO: Sabendo que o próprio repositório retorna um Optional, pode se fazer essa validação com um NotEmpty() or um OrElse()
//    private void IdsExceptionString(final String id) {
//        if ( !productRepository.verifyExistById(id) )
//            throw new NotFoundException("Not Found");
//    }
//
//    private void IdsExceptionListString(final List<String> ids) {
//
//        ids.forEach(this::IdsExceptionString);
//    }

//    TODO: Verificação deve ser feita no objeto de request, usando validators
//    private void verifyAllCorrectFields(final Product product) {
//
//        if ( product.getProductID() == null || product.getName() == null ||  product.getPrice() == null)
//            throw new MissingInputValuesException("Product fields are not valid");
//
//    }
}
