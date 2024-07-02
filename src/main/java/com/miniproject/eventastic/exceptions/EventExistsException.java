package com.miniproject.eventastic.exceptions;

public class EventExistsException extends RuntimeException {
  public EventExistsException(String message) {
    super(message);
  }

  public EventExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
