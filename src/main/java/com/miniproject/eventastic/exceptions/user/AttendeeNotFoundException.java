package com.miniproject.eventastic.exceptions.user;

public class AttendeeNotFoundException extends RuntimeException {

  public AttendeeNotFoundException(String message) {
    super(message);
  }

  public AttendeeNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
