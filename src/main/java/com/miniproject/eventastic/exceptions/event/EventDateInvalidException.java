package com.miniproject.eventastic.exceptions.event;

public class EventDateInvalidException extends RuntimeException {

  public EventDateInvalidException(String message) {
    super(message);
  }

  public EventDateInvalidException(String message, Throwable cause) {
    super(message, cause);
  }
}
