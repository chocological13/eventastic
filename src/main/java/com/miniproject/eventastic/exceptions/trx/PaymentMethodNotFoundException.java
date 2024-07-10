package com.miniproject.eventastic.exceptions.trx;

public class PaymentMethodNotFoundException extends RuntimeException {

  public PaymentMethodNotFoundException(String message) {
    super(message);
  }

  public PaymentMethodNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

}
