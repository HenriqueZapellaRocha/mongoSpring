package com.example.demo.handler;

import com.example.demo.exception.MissingInputValuesException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import com.example.demo.dtos.MissingInputValuesExceptionDTO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class MissingInputValuesExceptionHandler {

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    @ExceptionHandler(MissingInputValuesException.class)
    public MissingInputValuesExceptionDTO handler(final MissingInputValuesException e) {
        return new MissingInputValuesExceptionDTO(e.getMessage());
    }
}
