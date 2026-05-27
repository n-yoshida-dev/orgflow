package com.yoshida.orgflow.common.exception;

public class InvalidJwtSubjectException extends RuntimeException {
  public InvalidJwtSubjectException(String message, Throwable cause) {
    super(message, cause);
  }

}
