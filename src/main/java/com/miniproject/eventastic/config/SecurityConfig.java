package com.miniproject.eventastic.config;

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
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

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
  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return (request, response, accessDeniedException) -> {
      // Customize the response for 403 Forbidden
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.getWriter().write("Access denied!");
    };
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
  }

  @Bean
  public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
    return new SimpleUrlAuthenticationFailureHandler("/login?error");
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
//          auth.requestMatchers("/api/**").permitAll();

          // ! TODO: add roles related authorizations
          // * create event
          auth.requestMatchers("/api/v1/events/create/**").hasRole("ORGANIZER");

          // ! TODO: uncomment this when all the security set up is ready
//          auth.anyRequest().authenticated();
          // > for dev
          auth.requestMatchers("/**").hasRole("SUPERCAT");
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
