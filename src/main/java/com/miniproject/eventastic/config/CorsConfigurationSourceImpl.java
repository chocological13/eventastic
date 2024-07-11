package com.miniproject.eventastic.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class CorsConfigurationSourceImpl implements CorsConfigurationSource {

  @Override
  public CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
    corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:8080",
        "http://localhost:3000",
        "https://eventastic-ol7zwytd3q-as.a.run.app", "https://eventastic-app-git-dev-yeefs-projects.vercel.app"));
    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE"));
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setExposedHeaders(
        List.of("Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
    return corsConfiguration;
  }
}
