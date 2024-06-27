package com.miniproject.eventastic.auth.service.impl;

import com.miniproject.eventastic.auth.entity.dto.forgorPassword.ForgotPasswordRequestDto;
import com.miniproject.eventastic.auth.entity.dto.forgorPassword.ForgotPasswordResponseDto;
import com.miniproject.eventastic.auth.entity.dto.resetPassword.ResetPasswordRequestDto;
import com.miniproject.eventastic.auth.helpers.UrlBuilder;
import com.miniproject.eventastic.auth.repository.ForgotPasswordRedisRepository;
import com.miniproject.eventastic.auth.service.ForgotPasswordService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

  private final UsersRepository usersRepository;
  private final UsersService usersService;
  private final ForgotPasswordRedisRepository forgotPasswordRedisRepository;
  private final UrlBuilder urlBuilder;


  @Override
  public String generateResetToken(String username) throws NoSuchAlgorithmException, JOSEException {
    SecretKey secretKey = KeyGenerator.getInstance("HmacSHA256").generateKey();
    byte[] signingKey = secretKey.getEncoded();

    // Create JWT claims with a shorter "jti" claim
    String jti = UUID.randomUUID().toString().substring(0, 8);

    // Create the JWT Claims Set
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(username)
        .jwtID(jti)
        .expirationTime(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(6)))
        .build();

    // Create the signed JWT
    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    signedJWT.sign(new MACSigner(signingKey));

    // Jwt string and then map it to the TokenData
    String token = signedJWT.serialize();

    // Return the serialized JWT as a string
    return signedJWT.serialize();
  }


  @Override
  public ForgotPasswordResponseDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto)
      throws NoSuchAlgorithmException, JOSEException {
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
      forgotPasswordRedisRepository.saveResetToken(randomToken, token, username);

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
  public Boolean resetPassword(String resetToken, ResetPasswordRequestDto requestDto) throws Exception {
    // check for validity
    boolean isValid = forgotPasswordRedisRepository.isValidResetToken(resetToken);
    if (isValid) {
      // look for User
      String username = forgotPasswordRedisRepository.getUsernameFromToken(resetToken);
      log.info("Retrieved username: {} for reset token: {}", username, resetToken);
      Optional<Users> userOptional = usersRepository.findByUsername(username);
      if (userOptional.isPresent()) {
        Users user = userOptional.get();
        if (requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
          usersService.resetPassword(user, requestDto.getNewPassword());
          forgotPasswordRedisRepository.blacklistResetToken(resetToken);
          return true;
        } else {
          throw new Exception("Password doesn't match");
        }
      } else {
        throw new Exception("User not found");
      }
    }
    return false;
  }
}

//  @Override
//  public void resetPassword(String username, String token, ResetPasswordRequestDto requestDto) {
//    // check for validity of token
//    boolean isValid = authRedisRepository.isValid(username, token);
//    if (!isValid) {
//      // check if request body is valid (password match)
//      if (requestDto.getNewPassword() == requestDto.getConfirmPassword()) {
//        Optional<Users> userOptional = usersRepository.findByUsername(username);
//        if (userOptional.isPresent()) {
//          Users user = userOptional.get();
//          user.setPassword(requestDto.getNewPassword());
//        }
//      }
//    }
//  }

