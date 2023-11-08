package com.deundeunhaku.reliablekkuserver.common.security.filter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.deundeunhaku.reliablekkuserver.common.exception.NotAuthorizedException;
import com.deundeunhaku.reliablekkuserver.jwt.util.JwtTokenUtils;
import com.deundeunhaku.reliablekkuserver.member.service.MemberDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private final List<String> WHITELIST_URL =
      List.of(
          "/api/v1/auth/admin/login",
          "/api/v1/auth/admin/logout",
          "/api/v1/auth/login",
          "/api/v1/find-password/phone-number/certification-number",
          "/api/v1/find-password",
          "/api/v1/register/phone-number/duplicate",
          "/api/v1/register/phone-number/certification-number",
          "/api/v1/register",
          "/api/v1/order/sse/connect",
          "/api/v1/admin/order/sse/connect",
          "/docs/index.html",
          "/api/v1/token/valid",
          "/api/v1/token/update"
      );

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestURI = request.getRequestURI();

    if (WHITELIST_URL.contains(requestURI)) {
      filterChain.doFilter(request, response);
      return;
    }

    log.info("requestURI: {}", requestURI);

//    Cookie[] cookies = request.getCookies();
//    String accessToken = null;
//
//    if (cookies != null) {
//      for (Cookie cookie : cookies) {
//        if (cookie.getName().equals("accessToken")) {
//          accessToken = cookie.getValue();
//        }
//      }
//    }

    String accessToken = parseBearerToken(request);

    String phoneNumber = jwtTokenUtils.getPhoneNumber(accessToken);
    log.info("phoneNumber: {}", phoneNumber);

    Boolean isTokenValid = jwtTokenUtils.validate(accessToken, phoneNumber);
    log.info("isTokenValid: {}", isTokenValid);

    if (!isTokenValid) {
      throw new NotAuthorizedException("유효하지 않은 토큰입니다.");
    }

    Boolean isTokenExpired = jwtTokenUtils.isTokenExpired(accessToken);
    log.info("isTokenExpired: {}", isTokenExpired);
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

  private String parseBearerToken(HttpServletRequest request) {

    String accessToken = request.getHeader(AUTHORIZATION);

    if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
      return accessToken.substring(7);
    } else {
      throw new NotAuthorizedException("잘못된 토큰입니다.");
    }
  }
}
