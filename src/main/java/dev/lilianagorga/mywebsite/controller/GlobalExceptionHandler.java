package dev.lilianagorga.mywebsite.controller;

import dev.lilianagorga.mywebsite.exception.EmailSendingException;
import dev.lilianagorga.mywebsite.exception.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessage handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }

    return ErrorMessage.builder()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .timestamp(new Date())
            .message("Validation failed")
            .details(errors)
            .build();
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorMessage handleBadCredentialsException(BadCredentialsException ex) {
    return ErrorMessage.builder()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .timestamp(new Date())
            .message("Invalid credentials")
            .details(Map.of("error", ex.getMessage()))
            .build();
  }

  @ExceptionHandler(EmailSendingException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorMessage handleEmailSendingException(EmailSendingException ex) {
    return ErrorMessage.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .timestamp(new Date())
            .message("Error occurred while sending email")
            .details(Map.of("error", ex.getMessage()))
            .build();
  }
}