package com.miniproject.eventastic.exceptions.trx;

public class OrganizerWalletNotFound extends RuntimeException {

  public OrganizerWalletNotFound(String message) {
    super(message);
  }

  public OrganizerWalletNotFound(String message, Throwable cause) {
    super(message, cause);
  }
}
