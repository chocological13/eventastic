package com.miniproject.eventastic.config;

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
import jakarta.servlet.http.HttpServletRequest;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Log
public class SecurityConfig {
  private final EnvConfigurationProperties envConfigurationProperties;
  private final RsaKeyConfigProperties rsaKeyConfigProperties;
  private final UserDetailsServiceImpl userDetailsService;
  private final CorsConfigurationSourceImpl corsConfigurationSource;

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
  public JwtEncoder jwtEncoder() throws Exception {
    var publicKey = rsaKeyConfigProperties.publicKey();
    var privateKey = rsaKeyConfigProperties.privateKey();
    if (envConfigurationProperties.toString().equals("production")) {
      String publicKeyString = System.getenv("PUBLIC_KEY");
      String privateKeyString = System.getenv("PRIVATE_KEY");

      publicKey = (RSAPublicKey) parsePublicKey(publicKeyString);
      privateKey = (RSAPrivateKey) parsePrivateKey(privateKeyString);
    }

    // make RSA JWK
    JWK rsaJwk =
        new RSAKey.Builder(publicKey).privateKey(privateKey).build();
    // define JWK Set
    JWKSet jwkSet = new JWKSet(rsaJwk);

    // make JWK source and return the encoder
    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);
    return new NimbusJwtEncoder(jwkSource);
  }

  private PublicKey parsePublicKey(String key) throws Exception {
    String publicKeyPEM = key
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s+", "");
    byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
    return keyFactory.generatePublic(keySpec);
  }

  private PrivateKey parsePrivateKey(String key) throws Exception {
    String privateKeyPEM = key
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s+", "");
    byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
    return keyFactory.generatePrivate(keySpec);
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(rsaKeyConfigProperties.publicKey()).build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(Directive.ALL));

    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers("/error/**", "/api/v1/auth/**", "/api/v1/users/register/**").permitAll();
          auth.requestMatchers(HttpMethod.GET, "/api/v1/events/**").permitAll();
          auth.requestMatchers("/api/v1/events/create/**", "/api/v1/events/{eventId}/update/**",
              "/api/v1/dashboard/**").hasAuthority(
              "SCOPE_ROLE_ORGANIZER");
          auth.anyRequest().authenticated();
        })
        .exceptionHandling(e -> {
          e.accessDeniedHandler(new CustomAccessDeniedHandler());
          e.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        })
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.decoder(jwtDecoder()))
            .bearerTokenResolver(this::resolveBearerToken)
        )
        .userDetailsService(userDetailsService)
        .httpBasic(Customizer.withDefaults())
        .formLogin(auth -> auth.failureHandler(new CustomAuthenticationFailureHandler()))
        .logout(logout -> logout.addLogoutHandler(clearSiteData))
        .build();
  }

  private String resolveBearerToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("JSESSIONID".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }

    String bearerHeader = request.getHeader("Authorization");
    if (bearerHeader != null && bearerHeader.startsWith("Bearer ")) {
      return bearerHeader.substring(7);
    }

    return null;
  }



}
