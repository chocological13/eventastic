package com.miniproject.eventastic.exceptions.user;

public class DuplicateCredentialsException extends RuntimeException {

  public DuplicateCredentialsException(String message) {
    super(message);
  }

  public DuplicateCredentialsException(String message, Throwable cause) {
    super(message, cause);
  }
}
