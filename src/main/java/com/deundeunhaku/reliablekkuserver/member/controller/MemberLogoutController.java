package com.deundeunhaku.reliablekkuserver.member.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/my-pages")
public class MemberLogoutController {

  @GetMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response,
      @CookieValue(name = "refreshToken", required = false) Cookie refreshTokenCookie) {
    if (refreshTokenCookie != null) {
      refreshTokenCookie.setMaxAge(0);
      response.addCookie(refreshTokenCookie);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }
}
