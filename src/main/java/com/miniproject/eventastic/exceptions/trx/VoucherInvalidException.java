package com.miniproject.eventastic.exceptions.trx;

public class VoucherInvalidException extends RuntimeException {

  public VoucherInvalidException(String message) {
    super(message);
  }

  public VoucherInvalidException(String message, Throwable cause) {
    super(message, cause);
  }
}
