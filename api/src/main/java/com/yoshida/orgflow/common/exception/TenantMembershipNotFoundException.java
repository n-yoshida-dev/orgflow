package com.yoshida.orgflow.common.exception;

public class TenantMembershipNotFoundException extends RuntimeException {

  public TenantMembershipNotFoundException(String message) {
    super(message);
  }
}
