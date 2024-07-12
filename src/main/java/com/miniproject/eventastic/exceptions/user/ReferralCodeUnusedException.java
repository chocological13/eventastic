package com.miniproject.eventastic.exceptions.user;

public class ReferralCodeUnusedException extends RuntimeException {

  public ReferralCodeUnusedException(String message) {
    super(message);
  }

  public ReferralCodeUnusedException(String message, Throwable cause) {
    super(message, cause);
  }
}
