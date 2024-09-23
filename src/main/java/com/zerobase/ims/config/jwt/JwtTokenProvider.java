package com.zerobase.ims.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.token.access-expire}")
  private long accessTokenTime;

  @Value("${jwt.token.refresh-expire}")
  private long refreshTokenTime;

  private final RedisTemplate<String, String> redisTemplate;

  private Key getSigningKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String createAccessToken(String email) {
    Date now = new Date();
    log.info(email);
    return Jwts.builder()
        .setSubject(email)  // 이메일을 subject로 설정
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + accessTokenTime * 1000))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  // RefreshToken 생성 및 Redis에 저장
  public String createRefreshToken(String email) {
    Date now = new Date();
    String refreshToken = Jwts.builder()
        .setSubject(email)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + refreshTokenTime * 1000))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();

    // Redis에 RefreshToken 저장 (TTL 설정)
    redisTemplate.opsForValue().set("refreshToken:" + email, refreshToken, refreshTokenTime, TimeUnit.SECONDS);

    return refreshToken;
  }

  // Access Token 검증 (JWT 자체 검증)
  public boolean validateToken(String token) {
    try {
      log.info("Token is valid");

      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.info("Invalid token");

      return false;
    }
  }

  // Refresh Token 유효성 검증 (Redis에서 조회하여 확인)
  public boolean validateRefreshToken(String userId, String refreshToken) {
    String storedRefreshToken = redisTemplate.opsForValue().get("refreshToken:" + userId);
    return storedRefreshToken != null && storedRefreshToken.equals(refreshToken) && validateToken(refreshToken);
  }

  public String getEmailFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();  // 여기서 subject를 email로 설정
  }

  // 토큰 만료 시간 조회
  public long getExpiration(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    Date expiration = claims.getExpiration();
    return expiration.getTime() - System.currentTimeMillis();
  }

  // 로그아웃 시 토큰 블랙리스트 등록
  public void addToBlacklist(String token) {
    long expiration = getExpiration(token);
    redisTemplate.opsForValue().set("blacklist:" + token, "logout", expiration, TimeUnit.MILLISECONDS);
  }

  // 블랙리스트에 토큰이 있는지 확인
  public boolean isTokenInBlacklist(String token) {
    return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
  }
}
