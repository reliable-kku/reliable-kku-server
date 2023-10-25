package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.jwt.constants.TokenDuration;
import com.deundeunhaku.reliablekkuserver.jwt.util.JwtTokenUtils;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.LoginRequest;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class MemberAuthenticationController {

  private final JwtTokenUtils jwtTokenUtils;
  private final MemberService memberService;

  @PostMapping("/login")
  public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest,
      HttpServletResponse response) {

    Member member = memberService.login(loginRequest.phoneNumber(), loginRequest.password());

    String accessToken = jwtTokenUtils.generateJwtToken(member.getId(),
        TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

    String refreshToken = jwtTokenUtils.generateJwtToken(member.getId(),
        TokenDuration.REFRESH_TOKEN_DURATION.getDuration());

    setAccessTokenInCookie(accessToken, response);
    setRefreshTokenInCookie(refreshToken, response);

    return ResponseEntity.ok().build();
  }

  private void setRefreshTokenInCookie(String refreshToken, HttpServletResponse response) {
    Cookie refreshTokenCookie = new Cookie("refreshToken", "Bearer " + refreshToken);
    refreshTokenCookie.setMaxAge(TokenDuration.REFRESH_TOKEN_DURATION.getDurationInSecond());
    refreshTokenCookie.setHttpOnly(true);
    response.addCookie(refreshTokenCookie);
  }

  private void setAccessTokenInCookie(String accessToken, HttpServletResponse response) {
    Cookie accessTokenCookie = new Cookie("accessToken", "Bearer " + accessToken);
    accessTokenCookie.setMaxAge(TokenDuration.ACCESS_TOKEN_DURATION.getDurationInSecond());
    accessTokenCookie.setHttpOnly(true);
    response.addCookie(accessTokenCookie);
  }


}
