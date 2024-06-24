package com.miniproject.eventastic.auth.service.impl;

import com.miniproject.eventastic.auth.repository.AuthRedisRepository;
import com.miniproject.eventastic.auth.service.AuthService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final AuthRedisRepository authRedisRepository;
  private final JwtEncoder jwtEncoder;


  @Override
  public String generateToken(Authentication authentication) {

    // for iat later
    Instant now = Instant.now();

    // define scope
    String scope = authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(" "));

    // jwt claims
    JwtClaimsSet claimsSet = JwtClaimsSet.builder()
        .issuer("self")
        .issuedAt(now)
        .expiresAt(now.plus(1, ChronoUnit.HOURS))
        .subject(authentication.getName())
        .claim("scope", scope)
        .build();

    // encode jwt
    var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();

    // save in redis
    // ! todo: configure redis unable to connect
    authRedisRepository.saveJwtKey(authentication.getName(), jwt);

    // return
    return jwt;
  }
}
