package com.zerobase.ims.user.controller;

import com.zerobase.ims.common.exception.ErrorCode;
import com.zerobase.ims.config.jwt.JwtTokenProvider;
import com.zerobase.ims.config.jwt.JwtUtil;
import com.zerobase.ims.user.entity.dto.LoginDto;
import com.zerobase.ims.user.entity.dto.SignupDto;
import com.zerobase.ims.user.entity.dto.TokenDto;
import com.zerobase.ims.user.repository.UserRepository;
import com.zerobase.ims.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final JwtTokenProvider provider;
  private final UserService userService;
  private final UserRepository userRepository;

  // 회원가입
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody SignupDto signupDto) {
    userService.register(signupDto);
    return ResponseEntity.ok("회원가입이 완료되었습니다.");
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
    TokenDto tokenDto = userService.login(loginDto);  // 서비스 메서드 호출
    return ResponseEntity.ok(tokenDto);
  }

  // 로그아웃
  @PostMapping("/logout")
  public ResponseEntity<String> logout(@RequestHeader("X-ACCESS-TOKEN") String token) {
    // Bearer 토큰 처리
    token = JwtUtil.extractToken(token);
    // 로그아웃 처리 (Redis에서 RefreshToken 삭제)
    userService.logout(token);

    return ResponseEntity.ok("로그아웃 성공");
  }

  // Refresh Token을 통한 Access Token 재발급
  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshAccessToken(
      @RequestHeader("Authorization") String refreshToken) {

    // Bearer 토큰 처리
    refreshToken = JwtUtil.extractToken(refreshToken);

    // Refresh Token 유효성 검증 및 새로운 Access Token 재발급
    if (provider.validateRefreshToken(provider.getEmailFromToken(refreshToken), refreshToken)) {
      String userId = provider.getEmailFromToken(refreshToken);
      String newAccessToken = provider.createAccessToken(userId);
      return ResponseEntity.ok(new TokenDto(newAccessToken, refreshToken));
    } else {
      return ResponseEntity.status(401).body(ErrorCode.INVALID_TOKEN);
    }
  }
}
