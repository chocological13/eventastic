package com.miniproject.eventastic.exceptions.trx;

public class PointsTrxNotFoundException extends RuntimeException {
  public PointsTrxNotFoundException(String message) {
    super(message);
  }

  public PointsTrxNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
