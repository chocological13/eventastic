package com.miniproject.eventastic.auth.helpers;

import org.springframework.stereotype.Component;

@Component
public class UrlBuilder {

  private final String baseUrl = "http://localhost:8080";

  public String getResetTokenUrl (String token) {
    String forgotPasswordUrl = "/auth/reset-password";
    return baseUrl + forgotPasswordUrl + "?token=" + token;
  }
}
