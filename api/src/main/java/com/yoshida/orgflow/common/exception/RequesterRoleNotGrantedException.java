package com.yoshida.orgflow.common.exception;

public class RequesterRoleNotGrantedException extends RuntimeException {

  public RequesterRoleNotGrantedException(String message) {
    super(message);
  }
}
