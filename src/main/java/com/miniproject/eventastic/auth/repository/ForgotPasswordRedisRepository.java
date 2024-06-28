package com.miniproject.eventastic.auth.repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ForgotPasswordRedisRepository {

  private static final String RESET_TOKEN_PREFIX = "eventastic:reset:strings:";
  private static final String URL_USER_PREFIX = "eventastic:reset:user:";
  private static final String BLACKLIST_KEY_PREFIX = ":blacklisted";

  private final ValueOperations<String, String> ops;
  private final JwtDecoder jwtDecoder;

  // constructor with value ops
  public ForgotPasswordRedisRepository(RedisTemplate<String, String> redisTemplate,
      @Qualifier("jwtDecoder") JwtDecoder jwtDecoder) {
    this.ops = redisTemplate.opsForValue();
    this.jwtDecoder = jwtDecoder;
  }

  // * for reset token
  public void saveResetToken(String randomToken, String resetToken) {
    ops.set(RESET_TOKEN_PREFIX + randomToken, resetToken, 6, TimeUnit.HOURS);
  }


  public String getResetToken(String randomToken) {
    return ops.get(RESET_TOKEN_PREFIX + randomToken);
  }

  public String getUsername(String randomToken) {
    return jwtDecoder.decode(getResetToken(randomToken)).getSubject();
  }

  public void blacklistResetToken(String randomToken) {
    ops.set(RESET_TOKEN_PREFIX + getUsername(randomToken) + BLACKLIST_KEY_PREFIX, "true", 1, TimeUnit.HOURS);
  }

  public boolean isValidResetToken(String randomToken, String resetToken) {
    String tokenFromUrl = getResetToken(randomToken);
    String blacklistedResetToken = ops.get(RESET_TOKEN_PREFIX + getUsername(randomToken) + BLACKLIST_KEY_PREFIX);
    log.info("jwt: " + randomToken);
    log.info(blacklistedResetToken);
    return blacklistedResetToken == null && Objects.equals(resetToken, tokenFromUrl);
  }
}
