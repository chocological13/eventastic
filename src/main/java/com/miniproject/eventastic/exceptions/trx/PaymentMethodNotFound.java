package com.miniproject.eventastic.exceptions.trx;

public class PaymentMethodNotFound extends RuntimeException {

  public PaymentMethodNotFound(String message) {
    super(message);
  }

  public PaymentMethodNotFound(String message, Throwable cause) {
    super(message, cause);
  }

}
