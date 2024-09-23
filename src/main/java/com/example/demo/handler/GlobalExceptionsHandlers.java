package com.example.demo.handler;

import com.example.demo.dtos.CookieNotSetExceptionDTO;
import com.example.demo.dtos.MissingInputValuesExceptionDTO;
import com.example.demo.dtos.NotFoundExceptionDTO;
import com.example.demo.exception.CookieNotSetException;
import com.example.demo.exception.MissingInputValuesException;
import com.example.demo.exception.NotFoundException;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionsHandlers {

//    TODO: apenas 1 exception handler pode ser responsável por várias exceções
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException(final NoHandlerFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No endpoint for this found");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(CookieNotSetException.class)
    public CookieNotSetExceptionDTO handleCookieNotSetException(final CookieNotSetException e) {
        return new CookieNotSetExceptionDTO(e.getMessage());
    }

    //TODO: Este seria um retorno de BAD REQUEST
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    @ExceptionHandler(MissingInputValuesException.class)
    public MissingInputValuesExceptionDTO handleMissingInputValuesException(final MissingInputValuesException e) {
        return new MissingInputValuesExceptionDTO(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    public NotFoundExceptionDTO handleNotFoundException(final NotFoundException e) {
        return new NotFoundExceptionDTO(e.getMessage());
    }

}