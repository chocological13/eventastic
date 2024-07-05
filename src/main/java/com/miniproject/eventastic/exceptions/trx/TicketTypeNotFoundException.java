package com.miniproject.eventastic.exceptions.trx;

public class TicketTypeNotFoundException extends RuntimeException {

  public TicketTypeNotFoundException(String message) {
    super(message);
  }

  public TicketTypeNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
