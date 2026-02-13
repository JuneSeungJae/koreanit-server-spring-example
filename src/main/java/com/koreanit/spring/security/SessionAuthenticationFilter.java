package com.koreanit.spring.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.koreanit.spring.user.UserEntity;
import com.koreanit.spring.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SessionAuthenticationFilter extends OncePerRequestFilter {

  public static final String SESSION_USER_ID = "LOGIN_USER_ID";

  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;

  public SessionAuthenticationFilter(
      UserRepository userRepository,
      UserRoleRepository userRoleRepository) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
  }

  private boolean needsAuthInjection(Authentication a) {
    return (a == null) || (a instanceof AnonymousAuthenticationToken);
  }

  private List<SimpleGrantedAuthority> resolveAuthorities(Long userId) {

    List<String> roles = userRoleRepository.findRolesByUserId(userId);

    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    boolean hasUserRole = false;

    // // DB에서 조회한 Role 변환
    for (String role : roles) {
      if ("ROLE_USER".equals(role)) {
        hasUserRole = true;
      }
      authorities.add(new SimpleGrantedAuthority(role));
    }

    // 방어적 기본값: ROLE_USER 보장
    if (!hasUserRole) {
      authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    return authorities;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // 1. 현재 요청의 SecurityContext 획득
    SecurityContext context = SecurityContextHolder.getContext();

    // 2. 현재 Authentication 조회
    Authentication cur = context.getAuthentication();

    // 이미 인증이 있으면 재설정하지 않음 (단, 익명 인증은 덮어씀)
    if (needsAuthInjection(cur)) {

      HttpSession session = request.getSession(false);
      if (session != null) {
        Object v = session.getAttribute(SESSION_USER_ID);

        if (v instanceof Long userId) {

          try{
          UserEntity user = userRepository.findById(userId);

          LoginUser principal = new LoginUser(
              user.getId(),
              user.getUsername(),
              user.getNickname());

          Authentication auth = new UsernamePasswordAuthenticationToken(
              principal,
              null,
              resolveAuthorities(userId));

          // 3. Authentication을 SecurityContext에 주입
          context.setAuthentication(auth);
          } catch (EmptyResultDataAccessException e) {
            // 세션에 저장된 사용자 ID가 DB에 없는 경우, 인증 정보를 설정하지 않음
            // (예: 사용자 삭제 후 세션이 남아있는 경우)
            session.removeAttribute(SESSION_USER_ID);
          }
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}