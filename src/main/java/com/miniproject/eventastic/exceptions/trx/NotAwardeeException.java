package com.miniproject.eventastic.exceptions.trx;

public class NotAwardeeException extends RuntimeException {

  public NotAwardeeException(String message) {
    super(message);
  }

  public NotAwardeeException(String message, Throwable cause) {
    super(message, cause);
  }
}
