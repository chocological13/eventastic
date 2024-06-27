package com.miniproject.eventastic.auth.repository;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ForgotPasswordRedisRepository {

  private static final String RESET_TOKEN_PREFIX = "eventastic:reset:strings:";
  private static final String URL_USER_PREFIX = "eventastic:reset:user:";
  private static final String BLACKLIST_KEY_PREFIX = ":blacklisted";

  private final ValueOperations<String, String> ops;

  // constructor with value ops
  public ForgotPasswordRedisRepository(RedisTemplate<String, String> redisTemplate) {
    this.ops = redisTemplate.opsForValue();
  }

  // * for reset token
  public void saveResetToken(String randomToken, String resetToken, String username) {
    ops.set(RESET_TOKEN_PREFIX + randomToken, resetToken, 6, TimeUnit.HOURS);
    ops.set(URL_USER_PREFIX + randomToken, username, 6, TimeUnit.HOURS);
  }

  public void blacklistResetToken(String randomToken) {
    ops.set(RESET_TOKEN_PREFIX + randomToken + BLACKLIST_KEY_PREFIX, "true", 1, TimeUnit.HOURS);
    ops.set(URL_USER_PREFIX + randomToken + BLACKLIST_KEY_PREFIX, "true", 1, TimeUnit.HOURS);
  }

  public String getUsernameFromToken(String randomToken) {
    return ops.get(URL_USER_PREFIX + randomToken);
  }

  public String getResetToken(String randomToken) {
    return ops.get(RESET_TOKEN_PREFIX + randomToken);
  }

  public boolean isValidResetToken(String randomToken) {
    String resetToken = getResetToken(randomToken);
    String blacklistedResetToken = ops.get(RESET_TOKEN_PREFIX + randomToken + BLACKLIST_KEY_PREFIX);
    String blacklistedUsernameToken = ops.get(URL_USER_PREFIX + randomToken + BLACKLIST_KEY_PREFIX);
    return blacklistedResetToken == null && blacklistedUsernameToken == null && resetToken != null;
  }
}
