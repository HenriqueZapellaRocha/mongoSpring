package com.example.demo.v1.controller;

import java.util.LinkedList;
import java.util.List;

import com.example.demo.service.services.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.service.services.ProductService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;


    @PostMapping("/add")
    //TODO: Como estamos dentro da ProductController, não precisa colocar "product" nos métodos. Ex: add() ou create()
    //TODO: Não há necessidade de marcar as assinaturas como final
    public ProductEntity addProduct(@RequestBody final ProductRequestDTO product) {
        return productService.addProduct(product);
    }

    //TODO: O verbo HTTP é GET, logo é redundante repetir o get na rota. ex: /{id}
    @GetMapping("/get/{id}")
    public ProductEntity getById(@PathVariable final String id, final HttpServletResponse response) {
        final ProductEntity productEntity = productService.getById(id);
        CookieService.setCookie(response, "last", id);
        return productEntity;
    }

    //TODO: O verbo HTTP é GET, logo é redundante repetir o get na rota. ex: /last
    @GetMapping("/getLast")
    public ProductEntity getLast(final HttpServletRequest request) {
        final Cookie cookie = CookieService.getCookie(request, "last");

        return productService.getById(cookie.getValue());
    }

    //TODO: O verbo HTTP é GET, logo é redundante repetir o get na rota. ex: /
    @GetMapping("/getAll")
    public List<ProductEntity> getAll() {
        return productService.getAll();
    }

    //TODO: Esta lógica atualiza apenas 1 product
    @PutMapping("/updateAll/{id}")
    public ProductEntity updateAll(@RequestBody final ProductRequestDTO product, @PathVariable final String id) {
        return productService.updateProduct(product, id);
    }

    //TODO: O verbo HTTP é DELETE, logo é redundante repetir o delete na rota. ex: /{id}
    @DeleteMapping("/deleteById/{id}")
    public void deleteById(@PathVariable final String id) {
        final LinkedList<String> ids = new LinkedList<>();
        ids.add(id);
        productService.deleteProductAllById(ids);
    }

    //TODO: O verbo HTTP é DELETE, logo é redundante repetir o delete na rota. ex: /
    //TODO: Este método delete vários (many) e não todos (all)
    @DeleteMapping("/deleteAllById")
    public void deleteAllById(@RequestBody final List<String> id) {
        productService.deleteProductAllById(id);
    }
}
