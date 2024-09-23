package com.example.demo.handler;

import com.example.demo.dtos.CookieNotSetExceptionDTO;
import com.example.demo.exception.CookieNotSetException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CookieNotSetExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(CookieNotSetException.class)
    public CookieNotSetExceptionDTO handler(final CookieNotSetException e) {
        return new CookieNotSetExceptionDTO(e.getMessage());
    }
}
