package com.miniproject.eventastic.exceptions.trx;

public class InsufficientPointsException extends RuntimeException {

  public InsufficientPointsException(String message) {
    super(message);
  }

  public InsufficientPointsException(String message, Throwable cause) {
    super(message, cause);
  }
}
