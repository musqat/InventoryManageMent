package com.zerobase.ims.config.jwt;

import com.zerobase.ims.config.security.userDetails.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final UserDetailsServiceImpl userDetailsService;
  private final JwtTokenProvider provider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {

    // 요청에서 JWT 추출
    String token = getJwtFromRequest(request);

// JWT 유효성 검증 성공 시 인증 정보 설정
    if (token != null && provider.validateToken(token)) {
      // 블랙리스트 확인
      if (!provider.isTokenInBlacklist(token)) {
        // 토큰에서 userId 추출

        String email = provider.getEmailFromToken(token);  // email 추출
        log.info("Extracted email from token: {}", email);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);  // email로 조회

        // 인증 객체 생성
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        // 인증 정보를 SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    // 다음 필터 체인으로 요청을 전달
    chain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if (bearer == null) {
      bearer = request.getHeader("X-ACCESS-TOKEN");
    }

    if (bearer != null && bearer.startsWith("Bearer ")) {
      return bearer.substring(7); // "Bearer " 이후의 토큰 부분만 추출
    }
    return null;
  }
}
