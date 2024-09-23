package com.example.demo.handler;


import com.example.demo.dtos.MissingInputValuesExceptionDTO;
import com.example.demo.dtos.NotFoundExceptionDTO;
import com.example.demo.exception.MissingInputValuesException;
import com.example.demo.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NotFoundExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    public NotFoundExceptionDTO handler(final NotFoundException e) {
        return new NotFoundExceptionDTO(e.getMessage());
    }
}
