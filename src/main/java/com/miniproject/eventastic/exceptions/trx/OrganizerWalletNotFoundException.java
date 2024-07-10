package com.miniproject.eventastic.exceptions.trx;

public class OrganizerWalletNotFoundException extends RuntimeException {

  public OrganizerWalletNotFoundException(String message) {
    super(message);
  }

  public OrganizerWalletNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
