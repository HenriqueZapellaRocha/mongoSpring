package com.example.demo.handler;


import com.example.demo.dtos.CookieNotSetExceptionDTO;
import com.example.demo.dtos.InvalidInputValuesExceptionDTO;
import com.example.demo.dtos.NotFoundExceptionDTO;
import com.example.demo.exception.CookieNotSetException;
import com.example.demo.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionsHandlers {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    public NotFoundExceptionDTO handleNoHandlerFoundException(final NoHandlerFoundException e) {
        return new NotFoundExceptionDTO(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(CookieNotSetException.class)
    public CookieNotSetExceptionDTO handler(final CookieNotSetException e) {
        return new CookieNotSetExceptionDTO(e.getMessage());
    }

    // @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    // @ResponseBody
    // @ExceptionHandler(MissingInputValuesException.class)
    // public MissingInputValuesExceptionDTO handler(final MissingInputValuesException e) {
    //     return new MissingInputValuesExceptionDTO(e.getMessage());
    // }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    public NotFoundExceptionDTO handler(final NotFoundException e) {
        return new NotFoundExceptionDTO(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public InvalidInputValuesExceptionDTO handle(MethodArgumentNotValidException ex) {
    
        return new InvalidInputValuesExceptionDTO(ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(FieldError::getDefaultMessage)
        .toList());
        }
}