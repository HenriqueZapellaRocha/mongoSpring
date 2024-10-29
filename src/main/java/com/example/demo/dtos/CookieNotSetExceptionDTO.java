package com.example.demo.dtos;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Hidden
public class CookieNotSetExceptionDTO {

    @Schema( description = "The currency used in the price", example = "No cookie is set" )
    private String error;
}
