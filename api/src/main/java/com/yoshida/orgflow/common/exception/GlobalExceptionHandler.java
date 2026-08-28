package com.yoshida.orgflow.common.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    log.info("入力値が不正: {}", fieldErrors);

    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    ValidationErrorResponse response = new ValidationErrorResponse(status.value(), "入力値が不正です", fieldErrors);

    return ResponseEntity.status(status)
        .body(response);
  }

  @ExceptionHandler(AuthenticationFailedException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationFailed(AuthenticationFailedException ex) {

    log.info("認証失敗: {}", ex.getMessage());

    HttpStatus status = HttpStatus.UNAUTHORIZED;

    ErrorResponse response = new ErrorResponse(status.value(), "loginId または password が正しくありません");

    return ResponseEntity.status(status).body(response);
  }

  @ExceptionHandler(InvalidJwtSubjectException.class)
  public ResponseEntity<ErrorResponse> handleInvalidJwtSubject(InvalidJwtSubjectException ex) {

    log.warn("JWT の subject が不正", ex);

    HttpStatus status = HttpStatus.UNAUTHORIZED;

    ErrorResponse response = new ErrorResponse(status.value(), "認証情報が不正です");

    return ResponseEntity.status(status).body(response);
  }

  @ExceptionHandler(TenantMembershipNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTenantMembershipNotFound(TenantMembershipNotFoundException ex) {

    log.warn("所属していない tenant を指定", ex);

    HttpStatus status = HttpStatus.NOT_FOUND;

    ErrorResponse response = new ErrorResponse(status.value(), "対象のtenantが見つかりません");

    return ResponseEntity.status(status).body(response);
  }

  @ExceptionHandler(CurrentTenantNotSelectedException.class)
  public ResponseEntity<ErrorResponse> handleCurrentTenantNotSelected(CurrentTenantNotSelectedException ex) {

    log.info("tenantが未選択: {}", ex.getMessage());

    HttpStatus status = HttpStatus.FORBIDDEN;

    ErrorResponse response = new ErrorResponse(status.value(), "tenantが未選択です");

    return ResponseEntity.status(status).body(response);
  }

  @ExceptionHandler(InvalidJwtCurrentTenantIdException.class)
  public ResponseEntity<ErrorResponse> handleInvalidJwtCurrentTenantId(InvalidJwtCurrentTenantIdException ex) {

    log.warn("JWT の current_tenant_id が不正", ex);

    HttpStatus status = HttpStatus.UNAUTHORIZED;

    ErrorResponse response = new ErrorResponse(status.value(), "認証情報が不正です");

    return ResponseEntity.status(status).body(response);
  }

  @ExceptionHandler(InternalOrganizationMembershipNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleInternalOrganizationMembershipNotFound(
      InternalOrganizationMembershipNotFoundException ex) {

    log.warn("所属していない組織を指定", ex);

    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    ErrorResponse response = new ErrorResponse(status.value(), "指定された組織に所属していません");

    return ResponseEntity.status(status).body(response);
  }

  @ExceptionHandler(RequesterRoleNotGrantedException.class)
  public ResponseEntity<ErrorResponse> handleRequesterRoleNotGranted(RequesterRoleNotGrantedException ex) {

    log.info("申請作成の権限なし: {}", ex.getMessage());

    HttpStatus status = HttpStatus.FORBIDDEN;

    ErrorResponse response = new ErrorResponse(status.value(), "申請を作成する権限がありません");

    return ResponseEntity.status(status).body(response);
  }

}