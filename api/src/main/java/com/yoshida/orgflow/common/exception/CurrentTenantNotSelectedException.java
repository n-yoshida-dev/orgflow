package com.yoshida.orgflow.common.exception;

public class CurrentTenantNotSelectedException extends RuntimeException {
  public CurrentTenantNotSelectedException(String message) {
    super(message);
  }
}
