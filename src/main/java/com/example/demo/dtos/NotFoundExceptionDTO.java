package com.example.demo.dtos;




import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotFoundExceptionDTO {

    @Schema( description = "The currency used in the price", example = "No product found" )
    private String error;
}
