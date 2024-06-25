package com.miniproject.eventastic.auth.helpers;

import org.springframework.stereotype.Component;

@Component
public class UrlBuilder {

  private String baseUrl = "http://localhost:8080";

  public String getResetTokenUrl (String token) {
    String forgotPasswordUrl = "/forgot-password";
    return baseUrl + forgotPasswordUrl + "?token=" + token;
  }
}
