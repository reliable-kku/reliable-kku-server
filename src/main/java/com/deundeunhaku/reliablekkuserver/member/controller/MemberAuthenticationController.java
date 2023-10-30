package com.deundeunhaku.reliablekkuserver.member.controller;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import com.deundeunhaku.reliablekkuserver.jwt.constants.TokenDuration;
import com.deundeunhaku.reliablekkuserver.jwt.util.JwtTokenUtils;
import com.deundeunhaku.reliablekkuserver.member.dto.LoginRequest;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
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
  public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {

    memberService.login(loginRequest.phoneNumber(), loginRequest.password());

    String accessToken = jwtTokenUtils.generateJwtToken(loginRequest.phoneNumber(),
        TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

    String refreshToken = jwtTokenUtils.generateJwtToken(loginRequest.phoneNumber(),
        TokenDuration.REFRESH_TOKEN_DURATION.getDuration());

    ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
        .maxAge(TokenDuration.ACCESS_TOKEN_DURATION.getDurationInSecond())
        .httpOnly(true)
        .build();

    ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken",
        refreshToken)
        .maxAge(TokenDuration.REFRESH_TOKEN_DURATION.getDurationInSecond())
        .httpOnly(true)
        .build();

    return ResponseEntity.ok()
        .header(SET_COOKIE, accessTokenCookie.toString())
        .header(SET_COOKIE, refreshTokenCookie.toString())
        .build();
  }

}
