package com.yoshida.orgflow.common.exception;

public class InvalidJwtCurrentTenantIdException extends RuntimeException {
  public InvalidJwtCurrentTenantIdException(String message, Throwable cause) {
    super(message, cause);
  }
}
