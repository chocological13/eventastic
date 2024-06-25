package com.miniproject.eventastic.auth.repository;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class AuthRedisRepository {

  // prefix
  private static final String STRING_KEY_PREFIX = "eventastic:jwt:strings:";
  private static final String BLACKLIST_KEY_PREFIX = ":blacklisted";

  // value ops
  private final ValueOperations<String, String> ops;

  // constructor with value ops
  public AuthRedisRepository(RedisTemplate<String, String> redisTemplate) {
    this.ops = redisTemplate.opsForValue();
  }

  // save jwt key in redis
  public void saveJwtKey (String username, String jwtKey) {
    ops.set(STRING_KEY_PREFIX + username, jwtKey, 1, TimeUnit.HOURS);
  }

  // blacklist jwt key
  public void blacklistJwtKey (String username) {
    ops.set(STRING_KEY_PREFIX + username + BLACKLIST_KEY_PREFIX, "true", 1, TimeUnit.HOURS);
  }

  // get jwt key
  public String getJwtKey (String username) {
    return ops.get(STRING_KEY_PREFIX + username);
  }

  // check if it's valid
  public boolean isValid (String username, String jwtKey) {
    String storedKey = ops.get(STRING_KEY_PREFIX + username);
    String blacklisted = ops.get(STRING_KEY_PREFIX + username + BLACKLIST_KEY_PREFIX);
    return storedKey != null && blacklisted == null && storedKey.equals(jwtKey);
  }

}
