package com.zerobase.ims.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisUtil {

  private final RedisTemplate<String, String> redisTemplate;

  public void setDataExpire(String key, String value, long durationInSeconds) {
    redisTemplate.opsForValue().set(key, value, durationInSeconds);
  }

  public String getData(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public void deleteData(String key) {
    redisTemplate.delete(key);
  }
}
