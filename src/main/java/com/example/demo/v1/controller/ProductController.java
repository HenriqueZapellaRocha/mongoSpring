package com.example.demo.v1.controller;

import java.util.LinkedList;
import java.util.List;

import com.example.demo.dtos.CookieNotSetExceptionDTO;
import com.example.demo.dtos.InvalidInputValuesExceptionDTO;
import com.example.demo.dtos.NotFoundExceptionDTO;
import com.example.demo.service.services.CookieService;
import org.springframework.web.bind.annotation.RequestBody;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            description = "Add new product in api",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = ProductRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Product JSON request example",
                                            value = "{\"name\": \"CLANG\", \"price\": 200.0}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = ProductEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "product response example",
                                                    value = "{\n" +
                                                            "\"productID\": \"6ec58140-b159-4a5b-af91-3f976f8ebcb4\",\n" +
                                                            "\"name\": \"CLANG\",\n" +
                                                            "\"price\": 200.0\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            description = "When the request body is faulting things or invalid inputs",
                            responseCode = "400",
                            content = @Content(
                                    schema = @Schema(implementation = InvalidInputValuesExceptionDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\n" +
                                                            "\"errors\": "+"[\n"+
                                                            "\"price: blank price\","+
                                                            "\"name: blank name\"\n]"+
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            description = "When the currency informed is not found",
                            responseCode = "404",
                            content = @Content(
                                    schema = @Schema(implementation = NotFoundExceptionDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\n"+
                                                            "\"error\": \"currency not found\""+
                                                            "\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping("/add")
    public ProductEntity add( @RequestBody @Valid ProductRequestDTO product,
                            @RequestParam( name = "currency" ) String currency ) {
        return productService.add( product,currency, "USD" );
    }

    @Operation(
            description = "Add new product in api",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ProductEntity.class
                                    ),
                                    examples = @ExampleObject(
                                            name = "product response example",
                                            value = "{\n"+ "\"productID\": "+"\"6ec58140-b159-4a5b-af91-3f976f8ebcb4\"," +
                                                    "\n\"name\": "+"\"CLANG\"," +
                                                    "\n\"price\": "+"200.0" + "\n}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            description = "Error",
                            responseCode = "404",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = NotFoundExceptionDTO.class
                                    ),
                                    examples = @ExampleObject(
                                            name = "product id not found example",
                                            value = "{\n"+"\"error\": "+"\"No product found\"\n}"
                                    )
                            )
                    ),
            }
    )
    @GetMapping( "/{id}" )
    public ProductEntity getById( @PathVariable String id, HttpServletResponse response, 
                                        @RequestParam( name = "currency" ) String currency ) {

        final ProductEntity productEntity = productService.getById( id,"USD", currency );
        CookieService.setCookie( response, "last", id );
        return productEntity;
    }
    @Operation(
            description = "Get the last product consulted in get by id using cookie",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ProductEntity.class
                                    ),
                                    examples = @ExampleObject(
                                            name = "product response example",
                                            value = "{\n"+ "\"productID\": "+"\"6ec58140-b159-4a5b-af91-3f976f8ebcb4\"," +
                                                    "\n\"name\": "+"\"CLANG\"," +
                                                    "\n\"price\": "+"200.0" + "\n}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            description = "Error",
                            responseCode = "400",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = CookieNotSetExceptionDTO.class
                                    ),
                                    examples = @ExampleObject(
                                            name = "product id not found example",
                                            value = "{\n"+"\"error\": "+"\"No cookie is set\" \n}"
                                    )
                            )
                    ),
            }
    )
    @GetMapping( "/last" )
    public ProductEntity getLast( HttpServletRequest request,
                                 @RequestParam( name = "currency" ) String currency ) {
        final Cookie cookie = CookieService.getCookie( request, "last" );

        return productService.getById( cookie.getValue(),"USD",currency );
    }

    @Operation(
            description = "Get all products in API",
            responses = {
                    @ApiResponse(
                            description = "Sucess",
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ProductEntity[].class
                                    ),
                                    examples = @ExampleObject(
                                            name = "All products response example",
                                            value = "[\n" +
                                                    " {\n" +
                                                    "  \"productID\": \"6ec58140-b159-4a5b-af91-3f976f8ebcb4\",\n" +
                                                    "  \"name\": \"CLANG\",\n" +
                                                    "  \"price\": \"100.0\",\n" +
                                                    " }," +
                                                    " \n{\n" +
                                                    "  \"productID\": \"6ec58140-b159-4a5b-af91-3f976f8ebcb4\",\n" +
                                                    "  \"name\": \"GCC\",\n" +
                                                    "  \"price\": \"300.0\",\n" +
                                                    " \n}" +
                                                    "\n]"
                                    )
                            )
                    )
            }

    )
    @GetMapping( "/All" )
    public List<ProductEntity> getAll( HttpServletRequest request,
                    @RequestParam( name = "currency" ) String currency ) {
        return productService.getAll( "USD", currency );
    }

    @Operation(
            description = "Add new product in api",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = ProductRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Product JSON request example",
                                            value = "{\"name\": \"CLANG\", \"price\": 200.0}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = ProductEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "product response example",
                                                    value = "{\n" +
                                                            "\"productID\": \"6ec58140-b159-4a5b-af91-3f976f8ebcb4\",\n" +
                                                            "\"name\": \"CLANG\",\n" +
                                                            "\"price\": 200.0\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    @PutMapping( "/{id}" )
    public ProductEntity update( @RequestBody @Valid ProductRequestDTO product, @PathVariable String id ) {
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
