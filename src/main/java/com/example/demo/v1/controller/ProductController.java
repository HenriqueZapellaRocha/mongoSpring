package com.example.demo.v1.controller;

import java.security.ProtectionDomain;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.example.demo.repository.ProductRepository;
import com.example.demo.service.services.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Product;
import com.example.demo.service.services.ProductService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;


    @PostMapping("/add")
    public Product addProduct(@RequestBody final ProductRequestDTO product) {
        return productService.addProduct(product);
    }

    @GetMapping("/get/{id}")
    public Product getById(@PathVariable final String id, final HttpServletResponse response) {
        final Product product = productService.getById(id);
        CookieService.setCookie(response, "last", id);
        return product;
    }

    @GetMapping("/getLast")
    public Product getLast(final HttpServletRequest request) {
        final Cookie cookie = CookieService.getCookie(request, "last");

        return productService.getById(cookie.getValue());
    }

    @GetMapping("/getAll")
    public List<Product> getAll() {
        return productService.getAll();
    }

    @PutMapping("/updateAll/{id}")
    public Product updateAll(@RequestBody final ProductRequestDTO product, @PathVariable final String id) {
        return productService.updateProduct(product, id);
    }

    @DeleteMapping("/deleteById/{id}")
    public void deleteById(@PathVariable final String id) {
        final LinkedList<String> ids = new LinkedList<>();
        ids.add(id);
        productService.deleteProductAllById(ids);
    }

    @DeleteMapping("/deleteAllById")
    public void deleteAllById(@RequestBody final List<String> id) {
        productService.deleteProductAllById(id);
    }
}
