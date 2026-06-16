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
  public ResponseEntity<ErrorResponse> handleAuthenticationFailed(AuthenticationFailedException ex) {

    HttpStatus status = HttpStatus.UNAUTHORIZED;

    ErrorResponse response = new ErrorResponse(status.value(), "loginId または password が正しくありません");

    return ResponseEntity.status(status).body(response);
  }

  @ExceptionHandler(InvalidJwtSubjectException.class)
  public ResponseEntity<ErrorResponse> handleInvalidJwtSubject(InvalidJwtSubjectException ex) {

    HttpStatus status = HttpStatus.UNAUTHORIZED;

    ErrorResponse response = new ErrorResponse(status.value(), "認証情報が不正です");

    return ResponseEntity.status(status).body(response);
  }

  @ExceptionHandler(TenantMembershipNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTenantMembershipNotFound(TenantMembershipNotFoundException ex) {

    HttpStatus status = HttpStatus.NOT_FOUND;

    ErrorResponse response = new ErrorResponse(status.value(), "対象のtenantが見つかりません");

    return ResponseEntity.status(status).body(response);
  }

  @ExceptionHandler(CurrentTenantNotSelectedException.class)
  public ResponseEntity<ErrorResponse> handleCurrentTenantNotSelected(CurrentTenantNotSelectedException ex) {

    HttpStatus status = HttpStatus.FORBIDDEN;

    ErrorResponse response = new ErrorResponse(status.value(), "tenantが未選択です");

    return ResponseEntity.status(status).body(response);
  }

  @ExceptionHandler(InvalidJwtCurrentTenantIdException.class)
  public ResponseEntity<ErrorResponse> handleInvalidJwtCurrentTenantId(InvalidJwtCurrentTenantIdException ex) {

    HttpStatus status = HttpStatus.UNAUTHORIZED;

    ErrorResponse response = new ErrorResponse(status.value(), "認証情報が不正です");

    return ResponseEntity.status(status).body(response);
  }

}