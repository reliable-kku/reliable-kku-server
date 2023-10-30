package com.deundeunhaku.reliablekkuserver.jwt;

import static com.deundeunhaku.reliablekkuserver.jwt.constants.TokenDuration.ACCESS_TOKEN_DURATION;

import com.deundeunhaku.reliablekkuserver.jwt.constants.TokenDuration;
import com.deundeunhaku.reliablekkuserver.jwt.util.JwtTokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/token")
public class JwtController {

  private final JwtTokenUtils jwtTokenUtils;

  @GetMapping("/valid")
  public ResponseEntity<Void> isAccessTokenValid(HttpServletRequest request,
      HttpServletResponse response, @CookieValue(name = "accessToken") Cookie accessTokenCookie) {

    if (accessTokenCookie.getValue() == null || !accessTokenCookie.getValue().startsWith("Bearer ")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
    }

    String accessToken = accessTokenCookie.getValue().split("Bearer ")[1];

    String phoneNumber = jwtTokenUtils.getPhoneNumber(accessToken);
    Boolean validate = jwtTokenUtils.validate(accessToken, phoneNumber);

    if (validate) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @GetMapping("/update")
  public ResponseEntity<Void> updateAccessToken(
      @CookieValue(name = "refreshToken") Cookie refreshTokenCookie,
      HttpServletResponse response
  ) {

    String refreshToken = refreshTokenCookie.getValue();

    String phoneNumber = jwtTokenUtils.getPhoneNumber(refreshToken);
    Boolean validate = jwtTokenUtils.validate(refreshToken, phoneNumber);

    if (validate) {
      String newAccessToken = jwtTokenUtils.generateJwtToken(phoneNumber,
          ACCESS_TOKEN_DURATION.getDuration());
      setAccessTokenInCookie(newAccessToken, response);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  private void setAccessTokenInCookie(String accessToken, HttpServletResponse response) {
    Cookie accessTokenCookie = new Cookie("accessToken", "Bearer " + accessToken);
    accessTokenCookie.setMaxAge(TokenDuration.ACCESS_TOKEN_DURATION.getDurationInSecond());
    accessTokenCookie.setHttpOnly(true);
    response.addCookie(accessTokenCookie);
  }

}
