package com.miniproject.eventastic.exceptions.event;

public class EventEndedException extends RuntimeException {

  public EventEndedException(String message) {
    super(message);
  }

  public EventEndedException(String message, Throwable cause) {
    super(message, cause);
  }
}
