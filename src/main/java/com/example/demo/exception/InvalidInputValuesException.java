package com.example.demo.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema( hidden = true )
@Getter
public class InvalidInputValuesException extends RuntimeException {

    public InvalidInputValuesException(String message) {
        super(message);
    }
}
