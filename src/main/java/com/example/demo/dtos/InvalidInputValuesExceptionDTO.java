package com.example.demo.dtos;


import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InvalidInputValuesExceptionDTO {

    private List<String> errors;


}
