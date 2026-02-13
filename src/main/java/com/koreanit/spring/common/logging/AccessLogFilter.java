package com.koreanit.spring.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AccessLogFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

  private boolean isNoiseRequest(String uri) {
    return uri.equals("/favicon.ico")
        || uri.equals("/robots.txt")
        || uri.equals("/manifest.json")
        || uri.equals("/site.webmanifest")
        || uri.equals("/browserconfig.xml")

        // iOS / Android 아이콘
        || uri.startsWith("/apple-touch-icon")
        || uri.startsWith("/android-chrome")

        // Chrome / 브라우저 내부
        || uri.startsWith("/.well-known");
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();

    return uri.equals("/error");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String uri = request.getRequestURI();

    // 브라우저 자동 노이즈 요청은 여기서 즉시 종료
    if (isNoiseRequest(uri)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // 요청 1건을 식별하기 위한 Trace ID 생성
    String requestId = UUID.randomUUID().toString();

    // 로그 MDC에 저장 (같은 요청 로그 묶기)
    MDC.put("requestId", requestId);

    // 클라이언트에서도 확인 가능하도록 응답 헤더에 포함
    response.setHeader("X-Request-Id", requestId);

    long startTime = System.currentTimeMillis();

    try {
      // 실제 요청 처리
      filterChain.doFilter(request, response);
    } finally {
      long duration = System.currentTimeMillis() - startTime;

      log.info("{} {} -> {} ({} ms)",
          request.getMethod(),
          request.getRequestURI(),
          response.getStatus(),
          duration);

      // ThreadLocal 정리
      MDC.clear();
    }
  }
}