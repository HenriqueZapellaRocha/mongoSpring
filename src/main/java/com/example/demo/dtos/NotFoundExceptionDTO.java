package com.example.demo.dtos;


import java.util.List;

import org.springframework.validation.ObjectError;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotFoundExceptionDTO {

    private String message;
}
