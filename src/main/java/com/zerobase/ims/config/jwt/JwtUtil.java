package com.zerobase.ims.config.jwt;

public class JwtUtil {

  public static String extractToken(String token) {
    if (token != null && token.startsWith("Bearer ")) {
      return token.substring(7);
    }
    return token;
  }

}
