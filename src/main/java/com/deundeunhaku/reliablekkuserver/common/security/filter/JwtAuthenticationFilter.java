package com.deundeunhaku.reliablekkuserver.common.security.filter;

import com.deundeunhaku.reliablekkuserver.common.exception.NotAuthorizedException;
import com.deundeunhaku.reliablekkuserver.jwt.util.JwtTokenUtils;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import com.deundeunhaku.reliablekkuserver.member.service.MemberDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenUtils jwtTokenUtils;
  private final MemberDetailsService memberDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    Cookie[] cookies = request.getCookies();

    String accessToken = null;

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("accessToken")) {
          accessToken = cookie.getValue();
        }
      }
    }

    if (accessToken == null) {
      filterChain.doFilter(request, response);
      return;
    }

//    String accessToken = parseBearerToken(request);

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

  private String parseBearerToken(HttpServletRequest request) {

    String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
      return accessToken.substring(7);
    } else {
      throw new NotAuthorizedException("잘못된 토큰입니다.");
    }
  }
}
