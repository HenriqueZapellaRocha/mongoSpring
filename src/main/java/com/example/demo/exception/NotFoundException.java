package com.example.demo.exception;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(hidden = true)
public class NotFoundException extends RuntimeException {

  public NotFoundException(String message) {
        super(message);
    }
}
