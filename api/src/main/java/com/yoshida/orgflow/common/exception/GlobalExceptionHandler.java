package com.yoshida.orgflow.common.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  public record ValidationErrorResponse(
      int status,
      String message,
      List<ValidationFieldError> fieldErrors) {
  }

  public record ValidationFieldError(
      String field,
      String message) {
  }

  public record ErrorResponse(
      int status,
      String message) {
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {

    var fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fieldError -> new ValidationFieldError(fieldError.getField(), fieldError.getDefaultMessage()))
        .toList();

    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    ValidationErrorResponse response = new ValidationErrorResponse(status.value(), "入力値が不正です", fieldErrors);

    return ResponseEntity.status(status)
        .body(response);
  }

  @ExceptionHandler(AuthenticationFailedException.class)
  public ResponseEntity<ErrorResponse> handleAuthError(AuthenticationFailedException ex) {

    HttpStatus status = HttpStatus.UNAUTHORIZED;

    ErrorResponse response = new ErrorResponse(status.value(), "loginId または password が正しくありません");

    return ResponseEntity.status(status).body(response);
  }

}