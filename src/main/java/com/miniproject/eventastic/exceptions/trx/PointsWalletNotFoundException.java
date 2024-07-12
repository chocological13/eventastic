package com.miniproject.eventastic.exceptions.trx;

public class PointsWalletNotFoundException extends RuntimeException {

  public PointsWalletNotFoundException(String message) {
    super(message);
  }

  public PointsWalletNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
