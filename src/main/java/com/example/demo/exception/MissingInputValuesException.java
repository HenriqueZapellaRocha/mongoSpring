package com.example.demo.exception;

import lombok.Getter;

@Getter
public class MissingInputValuesException extends RuntimeException {

    public MissingInputValuesException(String message) {
        super(message);
    }
}
