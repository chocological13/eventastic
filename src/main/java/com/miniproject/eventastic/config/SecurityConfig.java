package com.miniproject.eventastic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.eventastic.auth.service.impl.UserDetailsServiceImpl;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Log
public class SecurityConfig {

  private final RsaKeyConfigProperties rsaKeyConfigProperties;
  private final UserDetailsServiceImpl userDetailsService;

  // password encoder
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // authentication manager
  @Bean
  public AuthenticationManager authenticationManager() {
    var authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return new ProviderManager(authProvider);
  }

  // jwt encoder and decoder
  @Bean
  public JwtEncoder jwtEncoder() {
    // make RSA JWK
    JWK rsaJwk =
        new RSAKey.Builder(rsaKeyConfigProperties.publicKey()).privateKey(rsaKeyConfigProperties.privateKey()).build();
    // define JWK Set
    JWKSet jwkSet = new JWKSet(rsaJwk);

    // make JWK source and return the encoder
    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);
    return new NimbusJwtEncoder(jwkSource);
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(rsaKeyConfigProperties.publicKey()).build();
  }

  // * access denied handler
  // 403
  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return (request, response, accessDeniedException) -> {
      response.setContentType("application/json;charset=UTF-8");
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      ObjectMapper objectMapper = new ObjectMapper();
      String errorMessage = "Access denied. You don't have enough permissions to access this resource.";
      response.getWriter().write(objectMapper.writeValueAsString(errorMessage));
    };
  }

  // 401
  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, authException) -> {
      response.setContentType("application/json;charset=UTF-8");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      ObjectMapper objectMapper = new ObjectMapper();
      String errorMessage = "Unauthorized access. Please log in to access this resource.";
      response.getWriter().write(objectMapper.writeValueAsString(errorMessage));
    };
  }

  @Bean
  public AuthenticationFailureHandler authenticationFailureHandler() {
    return (request, response, authException) -> {
      response.setContentType("application/json;charset=UTF-8");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      ObjectMapper objectMapper = new ObjectMapper();
      String errorMessage = "Authentication failed. Invalid username or password.";
      response.getWriter().write(objectMapper.writeValueAsString(errorMessage));
    };
  }


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        // * disables unused configs
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        // * endpoints authorization
        .authorizeHttpRequests(auth -> {

          auth.requestMatchers("/error/**").permitAll();
          auth.requestMatchers("/api/v1/auth/login/**").permitAll();
          auth.requestMatchers("/api/v1/users/register/**").permitAll();
          // ! TODO: dev purposes, delete when not used
          auth.requestMatchers("/api/**").permitAll();

          // ! TODO: add roles related authorizations
          // * create event
//          auth.requestMatchers("/api/v1/events/create/**").hasRole("ORGANIZER");

          // > for dev
//          auth.requestMatchers("/**").hasRole("SUPERCAT");

//          auth.anyRequest().authenticated();
        })
        // * exception handling
        .exceptionHandling(e -> {
          e.accessDeniedHandler(accessDeniedHandler());
          e.authenticationEntryPoint(authenticationEntryPoint());
        })
        // * session management
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // * oauth2 resource server to validate jwt
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())))
        // * configuring UserDetailsService, we will pass the one we already made
        .userDetailsService(userDetailsService)
        // * basic http authentication
        .httpBasic(Customizer.withDefaults())
        .build();
  }

}
