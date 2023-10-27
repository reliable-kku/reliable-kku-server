package com.deundeunhaku.reliablekkuserver.jwt;

import static com.deundeunhaku.reliablekkuserver.jwt.constants.TokenDuration.ACCESS_TOKEN_DURATION;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
      HttpServletResponse response) {

    String accessTokenWithPrefix = request.getHeader(AUTHORIZATION);

    if (accessTokenWithPrefix == null || !accessTokenWithPrefix.startsWith("Bearer ")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
    }

    String accessToken = accessTokenWithPrefix.split("Bearer ")[1];

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
      @CookieValue(name = "refreshToken") Cookie refreshTokenCookie
  ) {

    String refreshToken = refreshTokenCookie.getValue();

    String phoneNumber = jwtTokenUtils.getPhoneNumber(refreshToken);
    Boolean validate = jwtTokenUtils.validate(refreshToken, phoneNumber);

    if (validate) {
      String newAccessToken = jwtTokenUtils.generateJwtToken(phoneNumber,
          ACCESS_TOKEN_DURATION.getDuration());
      return ResponseEntity.ok().header(AUTHORIZATION, newAccessToken).build();
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }


}
