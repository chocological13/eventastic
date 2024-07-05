package com.miniproject.eventastic.exceptions.event;

public class DuplicateEventException extends RuntimeException {
  public DuplicateEventException(String message) {
    super(message);
  }

  public DuplicateEventException(String message, Throwable cause) {
    super(message, cause);
  }
}
