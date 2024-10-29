package com.example.demo.dtos;


import java.util.List;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Hidden
public class InvalidInputValuesExceptionDTO {

    @Schema(description = "message errors list",
            example = "[\"Blank name\", " +
                    "\"Negative number\"]")
    private List<String> errors;


}
