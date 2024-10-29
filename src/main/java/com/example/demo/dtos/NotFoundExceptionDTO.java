package com.example.demo.dtos;




import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Hidden
@Schema(hidden = true)
public class NotFoundExceptionDTO {

    @Schema( description = "The currency used in the price", example = "No product found" )
    private String error;
}
