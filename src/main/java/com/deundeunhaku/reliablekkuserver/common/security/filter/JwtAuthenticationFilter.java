package com.deundeunhaku.reliablekkuserver.common.security.filter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.deundeunhaku.reliablekkuserver.common.exception.NotAuthorizedException;
import com.deundeunhaku.reliablekkuserver.jwt.util.JwtTokenUtils;
import com.deundeunhaku.reliablekkuserver.member.service.MemberDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenUtils jwtTokenUtils;
  private final MemberDetailsService memberDetailsService;

  private static final List<String> EXCLUDE_URLS = List.of(
      "/docs",
      "/api/v1/login",
      "/api/v1/token",
      "/api/v1/auth",
      "/api/v1/find-password",
      "/api/v1/register",
      "/api/v1/register",
      "/api/v1/auth/admin",
      "/api/v1/order/sse",
      "/api/v1/admin/order/sse/connect",
      "/api/v1/admin/order/sse"
  );


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestURI = request.getRequestURI();

    log.info("requestURI: {}", requestURI);

    if (EXCLUDE_URLS.stream().anyMatch(requestURI::startsWith)) {
      filterChain.doFilter(request, response);
      return;
    }

    if (request.getHeader(AUTHORIZATION) == null || request.getHeader(AUTHORIZATION).isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = parseBearerToken(request);

    try {
      String phoneNumber = jwtTokenUtils.getPhoneNumber(accessToken);

      Boolean isTokenValid = jwtTokenUtils.validate(accessToken, phoneNumber);

      if (!isTokenValid) {
        throw new NotAuthorizedException("유효하지 않은 토큰입니다.");
      }

      Boolean isTokenExpired = jwtTokenUtils.isTokenExpired(accessToken);
      if (isTokenExpired) {
        throw new NotAuthorizedException("만료된 토큰입니다.");
      }

      UserDetails member = memberDetailsService.loadUserByUsername(phoneNumber);

      AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          member,
          null,
          member.getAuthorities()
      );

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
      securityContext.setAuthentication(authentication);

      SecurityContextHolder.setContext(securityContext);

      filterChain.doFilter(request, response);
    }
    catch (ExpiredJwtException e) {
      log.warn("토큰 만료, {}", jwtTokenUtils.getPhoneNumber(e.getClaims().getSubject()));
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setCharacterEncoding("UTF-8");
      try {
        response.getWriter().write("만료된 토큰입니다.");
      } catch (Exception ex) {
        log.error(ex.getMessage());
      }

    }
    catch (Exception e) {
      log.warn("알 수 없는 인증 에러, {}", e.getMessage());

      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setCharacterEncoding("UTF-8");
      try {
        response.getWriter().write("알 수 없는 인증 에러");
      } catch (Exception ex) {
        log.warn(ex.getMessage());
      }
    }
  }

  private String parseBearerToken(HttpServletRequest request) {

    String accessToken = request.getHeader(AUTHORIZATION);

    if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
      return accessToken.substring(7);
    } else {
      throw new NotAuthorizedException("잘못된 토큰입니다.");
    }
  }
}
