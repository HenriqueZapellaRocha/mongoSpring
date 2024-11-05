package com.example.demo.v1.controller;

import java.util.LinkedList;
import java.util.List;
import com.example.demo.dtos.CookieNotSetExceptionDTO;
import com.example.demo.dtos.InvalidInputValuesExceptionDTO;
import com.example.demo.dtos.NotFoundExceptionDTO;
import com.example.demo.service.services.CookieService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.servlet.http.Cookie;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation( description = "Add new product in api",
    responses = {
            @ApiResponse(responseCode = "400", description = "Invalid inputs in body", content = @Content(schema =
            @Schema(implementation = InvalidInputValuesExceptionDTO.class ))),
            @ApiResponse(responseCode = "404", description = "When the currency not found or product",
                    content = @Content(schema =
            @Schema(implementation = NotFoundExceptionDTO.class, example = "Not found currency"))), })
    @PostMapping( "/add" )
    public ProductResponseDTO add( @RequestBody @Valid ProductRequestDTO product,
                            @RequestParam( name = "currency" ) String currency ) {
        return productService.add( product,currency, "USD" );
    }

    @Operation( description = "Get any product from API using the ID",
    responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "\"When the currency not found or product\""
                    , content = @Content(schema = @Schema(implementation = NotFoundExceptionDTO.class ),
                    examples = { @ExampleObject(name = "Product not Found"),
                    @ExampleObject(name = "Currency not found", value = "{\"error\": \"currency not found\"}"),})),
            @ApiResponse(responseCode = "200") })
    @GetMapping( "/{id}" )
    public ProductResponseDTO getById( @PathVariable String id, HttpServletResponse response,
                                        @RequestParam( name = "currency" ) String currency ) {
        ProductResponseDTO product = productService.getById( id,"USD", currency );
        CookieService.setCookie( response, "last", id );
        return product;
    }

    @Operation( description = "Get the last product consulted in get by id using cookie",
            responses = {@ApiResponse(responseCode = "400", description = "No cookie is set",
                    content = @Content(schema = @Schema(implementation = CookieNotSetExceptionDTO.class ))) ,
                        @ApiResponse( responseCode = "404", description = "No product found",
                    content = @Content( schema = @Schema(implementation = NotFoundExceptionDTO.class ))) })
    @GetMapping( "/last" )
    public ProductResponseDTO getLast( HttpServletRequest request,
                                 @RequestParam( name = "currency" ) String currency ) {
        final Cookie cookie = CookieService.getCookie( request, "last" );

        return productService.getById( cookie.getValue(),"USD", currency );
    }

    @Operation( description = "Get all products in API",
    responses = {@ApiResponse(responseCode = "200", description = "Return all products", content =
    @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class)))),
    @ApiResponse(responseCode = "404", description = "Currency not found",
            content = @Content(schema = @Schema(implementation = NotFoundExceptionDTO.class ))),})
    @GetMapping( "/All" )
    public List<ProductResponseDTO> getAll( HttpServletRequest request,
                    @RequestParam( name = "currency" ) String currency ) {
        return productService.getAll( "USD", currency );
    }

    @Operation( description = "Update all information's of a product",
    responses = {@ApiResponse(responseCode = "404", description = "When currency not found", content =
    @Content(schema = @Schema(implementation = NotFoundExceptionDTO.class ), examples = {
            @ExampleObject(name = "Product not Found"),
            @ExampleObject(name = "Currency not found", value = "{\"error\": \"currency not found\"}"),
    })),
    @ApiResponse(responseCode = "200")})
    @PutMapping( "/{id}" )
    public ProductResponseDTO update( @RequestBody @Valid ProductRequestDTO product, @PathVariable String id ) {
        return productService.update( product, id );
    }

    @Operation( description = "Delete a product in api by the id",
            responses = {@ApiResponse( description = "OK", responseCode = "200") })
    @DeleteMapping( "/{id}" )
    public void deleteById( @PathVariable String id ) {
        final LinkedList<String> ids = new LinkedList<>();
        ids.add( id );
        productService.deleteMany( ids );
    }

    @Operation(
            description = "Deletes many products by a list of ids",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( content = @Content( schema = @Schema(
                                    implementation = ProductEntity[].class
                            ), examples = { @ExampleObject(
                                            name = "API request body example:",
                                            value = "[\n\"6ec58140-b159-4a5b-af91-3f976f8ebcb4\",\n" +
                                                    "\"34efad68-cb0b-47d3-a204-a677532f0ecc\",\n" +
                                                    "\"b7cc702c-97f5-419f-858e-17acf7f45d13\"\n\n]"
                                    )})))
    @DeleteMapping
    public void deleteMany( @RequestBody List<String> id ) {
        productService.deleteMany( id );
    }
}
