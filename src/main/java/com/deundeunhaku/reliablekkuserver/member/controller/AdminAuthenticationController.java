package com.deundeunhaku.reliablekkuserver.member.controller;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import com.deundeunhaku.reliablekkuserver.jwt.constants.TokenDuration;
import com.deundeunhaku.reliablekkuserver.jwt.util.JwtTokenUtils;
import com.deundeunhaku.reliablekkuserver.member.dto.AdminLoginRequest;
import com.deundeunhaku.reliablekkuserver.member.service.AdminMemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth/admin")
public class AdminAuthenticationController {

  private final JwtTokenUtils jwtTokenUtils;
  private final AdminMemberService adminMemberService;

  @PostMapping("/login")
  public ResponseEntity<Void> login(@Valid @RequestBody AdminLoginRequest loginRequest,
      HttpServletResponse response) {

    adminMemberService.login(loginRequest.username(), loginRequest.password());

    String accessToken = jwtTokenUtils.generateJwtToken(loginRequest.username(),
        TokenDuration.ACCESS_TOKEN_DURATION_ADMIN.getDuration());

    ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
        .maxAge(TokenDuration.ACCESS_TOKEN_DURATION.getDurationInSecond())
        .httpOnly(true)
        .build();

    return ResponseEntity.ok()
        .header(SET_COOKIE, accessTokenCookie.toString())
        .build();
  }

  @GetMapping("/logout")
  public ResponseEntity<Void> logout(@CookieValue(name = "accessToken") Cookie cookie,
      HttpServletResponse response) {
    setTokenMaxAgeZero(cookie, response);
    return ResponseEntity.ok().build();
  }

  private static void setTokenMaxAgeZero(Cookie cookie, HttpServletResponse response) {
    cookie.setValue("");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }


  private void setAccessTokenInCookie(String accessToken, HttpServletResponse response) {
    Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
    accessTokenCookie.setMaxAge(TokenDuration.ACCESS_TOKEN_DURATION_ADMIN.getDurationInSecond());
    accessTokenCookie.setHttpOnly(true);
    response.addCookie(accessTokenCookie);
  }

}
