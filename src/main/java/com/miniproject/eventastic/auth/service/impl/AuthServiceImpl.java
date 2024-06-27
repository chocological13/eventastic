package com.miniproject.eventastic.auth.service.impl;

import com.miniproject.eventastic.auth.entity.UserAuth;
import com.miniproject.eventastic.auth.entity.dto.resetPassword.ResetPasswordRequestDto;
import com.miniproject.eventastic.auth.entity.dto.forgorPassword.ForgotPasswordRequestDto;
import com.miniproject.eventastic.auth.entity.dto.forgorPassword.ForgotPasswordResponseDto;
import com.miniproject.eventastic.auth.entity.dto.login.LoginRequestDto;
import com.miniproject.eventastic.auth.entity.dto.login.LoginResponseDto;
import com.miniproject.eventastic.auth.helpers.UrlBuilder;
import com.miniproject.eventastic.auth.repository.AuthRedisRepository;
import com.miniproject.eventastic.auth.service.AuthService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.repository.UsersRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final AuthRedisRepository authRedisRepository;
  private final JwtEncoder jwtEncoder;
  private final UsersRepository usersRepository;
  private final UrlBuilder urlBuilder;


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

  @Override
  public ResponseEntity<?> login(LoginRequestDto loginRequestDto) {
    // * 1: authenticate user
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
    log.info("Authenticated user: {}", authentication);

    // * 2: store it in the security context
    SecurityContextHolder.getContext().setAuthentication(authentication);
    var ctx = SecurityContextHolder.getContext();
    ctx.setAuthentication(authentication);

    // * 3: get user's information
    UserAuth userDetails = (UserAuth) ctx.getAuthentication().getPrincipal();
    log.info("Principal: {}", userDetails);

    // ! 4: generate token
    String token = generateToken(authentication);

    // * 5: generate response
    LoginResponseDto response = new LoginResponseDto();
    response.setMessage("Welcome back, " + userDetails.getUsername() + "!");
    response.setToken(token);

    // * 6: create (response)cookie
    ResponseCookie cookie = ResponseCookie.from("JSESSIONID", token)
        .path("/")
        .httpOnly(true)
        .maxAge(3600)
        .build();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Set-Cookie", cookie.toString());

    // * 7: return the token
    return ResponseEntity.ok().headers(headers).body(response);
  }

  @Override
  public void logout() {
    // * Get logged in user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    String token = authRedisRepository.getJwtKey(username);

    if (token != null) {
      // * Invalidate token
      authRedisRepository.blacklistJwtKey(username);
    }
  }


}
