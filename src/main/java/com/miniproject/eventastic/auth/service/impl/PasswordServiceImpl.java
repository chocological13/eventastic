package com.miniproject.eventastic.auth.service.impl;

import com.miniproject.eventastic.auth.entity.dto.changePassword.ChangePasswordRequestDto;
import com.miniproject.eventastic.auth.entity.dto.forgorPassword.ForgotPasswordRequestDto;
import com.miniproject.eventastic.auth.entity.dto.forgorPassword.ForgotPasswordResponseDto;
import com.miniproject.eventastic.auth.entity.dto.resetPassword.ResetPasswordRequestDto;
import com.miniproject.eventastic.auth.helpers.UrlBuilder;
import com.miniproject.eventastic.auth.repository.ForgotPasswordRedisRepository;
import com.miniproject.eventastic.auth.service.PasswordService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class PasswordServiceImpl implements PasswordService {

  private final UsersRepository usersRepository;
  private final UsersService usersService;
  private final ForgotPasswordRedisRepository forgotPasswordRedisRepository;
  private final UrlBuilder urlBuilder;
  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;


  @Override
  public String generateResetToken(String username) {
    // Create JWT claims with a shorter "jti" claim
    String jti = UUID.randomUUID().toString().substring(0, 8);

    // Create the JWT Claims Set
    JwtClaimsSet claimsSet = JwtClaimsSet.builder()
        .subject(username)
        .id(jti)
        .expiresAt(Instant.now().plus(6, ChronoUnit.HOURS))
        .build();

    // Create the signed JWT
    return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
  }


  @Override
  public ForgotPasswordResponseDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto) {
    Optional<Users> userOptional = usersRepository.findByEmail(forgotPasswordRequestDto.getEmail());
    if (userOptional.isEmpty()) {
      return null;
    } else {
      Users user = userOptional.get();

      // generate random token to validate later (send to user's email)
      String username = user.getUsername();
      String token = generateResetToken(username);

      // Generate random string to map to the JWT -> so that the link doesn't include the whole token
      String randomToken = UUID.randomUUID().toString();

      // save shorter random token to redis
      forgotPasswordRedisRepository.saveResetToken(randomToken, token);

      // generate reset URL
      String resetTokenUrl = urlBuilder.getResetTokenUrl(randomToken);

      // set response
      ForgotPasswordResponseDto response = new ForgotPasswordResponseDto();
      response.setMessage("Find the link below");
      response.setResetTokenUrl(resetTokenUrl);

      // ! TODO: Send reset URL to email

      return response;
    }
  }

  @Override
  public void resetPassword(String urlToken, ResetPasswordRequestDto requestDto) {

    String resetToken = forgotPasswordRedisRepository.getResetToken(urlToken);
    String username = forgotPasswordRedisRepository.getUsername(urlToken);

    // check for validity
    boolean isValid = forgotPasswordRedisRepository.isValidResetToken(urlToken, resetToken);
    if (isValid) {
      // look for User
      Optional<Users> userOptional = usersRepository.findByUsername(username);
      if (userOptional.isPresent()) {
        Users user = userOptional.get();
        if (requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
          usersService.resetPassword(user, requestDto.getNewPassword());
          forgotPasswordRedisRepository.blacklistResetToken(urlToken);
        } else {
          throw new IllegalArgumentException("Password doesn't match");
        }
      }
    }
  }
}

