package com.zerobase.ims.user.service;

import static com.zerobase.ims.common.exception.ErrorCode.ALREADY_REGISTER_USER;

import com.zerobase.ims.common.exception.CustomException;
import com.zerobase.ims.common.exception.ErrorCode;
import com.zerobase.ims.config.jwt.JwtTokenProvider;
import com.zerobase.ims.config.redis.RedisUtil;
import com.zerobase.ims.user.entity.User;
import com.zerobase.ims.user.entity.dto.LoginDto;
import com.zerobase.ims.user.entity.dto.SignupDto;
import com.zerobase.ims.user.entity.dto.TokenDto;
import com.zerobase.ims.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider provider;
  private final RedisUtil redisUtil;

  // 회원가입 로직
  public User register(SignupDto signupDto) {
    // 이메일 중복 체크
    if (userRepository.findByEmail(signupDto.getEmail()).isPresent()) {
      throw new CustomException(ALREADY_REGISTER_USER);
    }

    // 유저 등록
    User user = User.builder()
        .email(signupDto.getEmail())
        .nickname(signupDto.getNickname())
        .role(signupDto.getRole())
        .password(passwordEncoder.encode(signupDto.getPassword()))
        .build();

    // 유저 저장
    return userRepository.save(user);
  }

  // 로그인 로직
  public TokenDto login(LoginDto loginDto) {
    // 이메일로 유저 조회
    User user = userRepository.findByEmail(loginDto.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

    // 비밀번호 검증
    if (!checkPassword(loginDto.getPassword(), user.getPassword())) {
      throw new CustomException(ErrorCode.LOGIN_CHECK_FAIL);
    }

    // AccessToken 및 RefreshToken 생성
    String accessToken = provider.createAccessToken(user.getEmail());
    String refreshToken = provider.createRefreshToken(user.getEmail());

    // Redis에 RefreshToken 저장 (만료시간 설정)
    redisUtil.setDataExpire("refreshToken:" + user.getEmail(), refreshToken,
        provider.getExpiration(refreshToken));

    return new TokenDto(accessToken, refreshToken);
  }


  // 로그인 시 비밀번호 확인을 위한 메서드
  public boolean checkPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  // RefreshToken을 사용하여 새로운 AccessToken 발급
  public String refreshAccessToken(String refreshToken) {
    // RefreshToken 유효성 검증
    if (!provider.validateRefreshToken(provider.getEmailFromToken(refreshToken), refreshToken)) {
      throw new CustomException(ErrorCode.TOKEN_EXPIRE);
    }

    String email = provider.getEmailFromToken(refreshToken);

    // Redis에서 저장된 RefreshToken 가져오기
    String storedRefreshToken = redisUtil.getData("refreshToken:" + email);
    if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
      throw new CustomException(ErrorCode.TOKEN_EXPIRE);
    }

    // 새로운 AccessToken 생성 후 반환
    return provider.createAccessToken(email);
  }

  // 로그아웃: Redis에서 저장된 RefreshToken을 삭제
  public void logout(String token) {
    try {
      String email = provider.getEmailFromToken(token);

      // Redis에서 해당 사용자의 RefreshToken 삭제
      redisUtil.deleteData("refreshToken:" + email);
    } catch (Exception e) {
      throw new CustomException(ErrorCode.LOGOUT_FAIL);
    }
  }
}
