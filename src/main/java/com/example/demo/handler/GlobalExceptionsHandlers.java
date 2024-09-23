package com.example.demo.handler;


import com.example.demo.dtos.NotFoundExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionsHandlers {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    public NotFoundExceptionDTO handleNoHandlerFoundException(final NoHandlerFoundException e) {
        return new NotFoundExceptionDTO(e.getMessage());
    }
}