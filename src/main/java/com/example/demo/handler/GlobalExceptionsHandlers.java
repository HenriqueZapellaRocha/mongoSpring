package com.example.demo.handler;


import com.example.demo.dtos.CookieNotSetExceptionDTO;
import com.example.demo.dtos.GlobalExceptionDTO;
import com.example.demo.dtos.InvalidInputValuesExceptionDTO;
import com.example.demo.dtos.NotFoundExceptionDTO;
import com.example.demo.exception.CookieNotSetException;
import com.example.demo.exception.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@Hidden
@RestControllerAdvice
public class GlobalExceptionsHandlers {

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ResponseBody
    @ExceptionHandler( NoHandlerFoundException.class )
    public NotFoundExceptionDTO handleNoHandlerFoundException( final NoHandlerFoundException e ) {
        return new NotFoundExceptionDTO( e.getMessage() );
    }

@ResponseStatus( HttpStatus.NOT_FOUND )
@ResponseBody
@ExceptionHandler( HttpClientErrorException.class )
public NotFoundExceptionDTO handler( final HttpClientErrorException e ) {
    try {

        String responseBody = e.getResponseBodyAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree( responseBody );

        String message = root.path( "message" ).asText();

        return new NotFoundExceptionDTO( message );
    } catch ( Exception ex ) {
        return new NotFoundExceptionDTO( "Erro desconhecido" );
    }
}


    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ResponseBody
    @ExceptionHandler( CookieNotSetException.class )
    public CookieNotSetExceptionDTO handler( final CookieNotSetException e ) {
        return new CookieNotSetExceptionDTO( e.getMessage() );
    }

    @ResponseStatus( HttpStatus.NOT_FOUND )
    @ResponseBody
    @ExceptionHandler( NotFoundException.class )
    public NotFoundExceptionDTO handler( final NotFoundException e ) {
        return new NotFoundExceptionDTO( e.getMessage() );
    }

    @ResponseStatus( HttpStatus.BAD_REQUEST )
    @ResponseBody
    @ExceptionHandler( MethodArgumentNotValidException.class )
    public InvalidInputValuesExceptionDTO handle( MethodArgumentNotValidException ex ) {
        return new InvalidInputValuesExceptionDTO( ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map( FieldError::getDefaultMessage )
        .toList() );
        }

    @ExceptionHandler(Exception.class)
    public GlobalExceptionDTO handleGlobalException(Exception ex, WebRequest request) {
        return new GlobalExceptionDTO( "An unknown error occurred. Please consult the support for resolution." );
    }
}