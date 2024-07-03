package com.miniproject.eventastic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.eventastic.auth.service.impl.UserDetailsServiceImpl;
import com.miniproject.eventastic.exceptions.CustomAccessDeniedHandler;
import com.miniproject.eventastic.exceptions.CustomAuthenticationEntryPoint;
import com.miniproject.eventastic.exceptions.CustomAuthenticationFailureHandler;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
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

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        // * disables unused configs
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        // * endpoints authorization
        .authorizeHttpRequests(auth -> {

          auth.requestMatchers("/error/**").permitAll();
          auth.requestMatchers("/api/v1/auth/**").permitAll();
          auth.requestMatchers("/api/v1/users/register/**").permitAll();
          // ! TODO: dev purposes, delete when not used
          auth.requestMatchers("/api/**").permitAll();

          // ! TODO: add roles related authorizations
          // * event management - only organizer
          auth.requestMatchers("/api/v1/events/create/**").hasAuthority("SCOPE_ROLE_ORGANIZER");
          auth.requestMatchers("/api/v1/events/{eventId}/update/**").hasAuthority("SCOPE_ROLE_ORGANIZER");
//          auth.requestMatchers("/**").authenticated();

          // > for dev
//          auth.requestMatchers("/**").hasAuthority("SCOPE_ROLE_SUPERCAT");

//          auth.anyRequest().authenticated();
        })
        // * exception handling
        .exceptionHandling(e -> {
          e.accessDeniedHandler(new CustomAccessDeniedHandler()); // 403
          e.authenticationEntryPoint(new CustomAuthenticationEntryPoint()); //401
        })
        // * session management
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // * oauth2 resource server to validate jwt and extract cookie
        .oauth2ResourceServer((oauth2) -> {
          oauth2.jwt((jwt) -> jwt.decoder(jwtDecoder()));
          oauth2.bearerTokenResolver((request) -> {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
              for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                  return cookie.getValue();
                }
              }
            }
            return null;
          });
        })
        // * configuring UserDetailsService, we will pass the one we already made
        .userDetailsService(userDetailsService)
        // * basic http authentication
        .httpBasic(Customizer.withDefaults())
        .formLogin(auth -> auth.failureHandler(new CustomAuthenticationFailureHandler()))//401
        .formLogin(Customizer.withDefaults())
        .build();
  }

}
