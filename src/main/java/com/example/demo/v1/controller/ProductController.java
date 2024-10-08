package com.example.demo.v1.controller;

import java.util.LinkedList;
import java.util.List;

import com.example.demo.service.services.CookieService;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.example.demo.repository.entity.ProductEntity;
import com.example.demo.service.services.ProductService;

@RequiredArgsConstructor
@RestController
@RequestMapping( "/product" )
public class ProductController {

    private final ProductService productService;


    @PostMapping( "/add" )
    public ProductEntity add( @RequestBody @Valid ProductRequestDTO product,
                            @RequestParam( name = "currency" ) String currency ) {
        return productService.add( product,currency, "USD" );
    }

    @GetMapping( "/{id}" )
    public ProductEntity getById( @PathVariable String id, HttpServletResponse response, 
                                        @RequestParam( name = "currency" ) String currency ) {

        final ProductEntity productEntity = productService.getById( id,"USD", currency );
        CookieService.setCookie( response, "last", id );
        return productEntity;
    }

    @GetMapping( "/last" )
    public ProductEntity getLast( HttpServletRequest request,
                                 @RequestParam( name = "currency" ) String currency ) {
        final Cookie cookie = CookieService.getCookie( request, "last" );

        return productService.getById( cookie.getValue(),"USD",currency );
    }

    @GetMapping( "/All" )
    public List<ProductEntity> getAll( HttpServletRequest request,
                    @RequestParam( name = "currency" ) String currency ) {
        return productService.getAll( "USD", currency );
    }

    @PutMapping( "/updateProduct/{id}" )
    public ProductEntity updateAll( @RequestBody @Valid ProductRequestDTO product, @PathVariable String id ) {
        return productService.update( product, id );
    }

    @DeleteMapping( "/{id}" )
    public void deleteById( @PathVariable String id ) {
        final LinkedList<String> ids = new LinkedList<>();
        ids.add( id );
        productService.deleteMany( ids );
    }

    @DeleteMapping
    public void deleteMany( @RequestBody List<String> id ) {
        productService.deleteMany( id );
    }
}
