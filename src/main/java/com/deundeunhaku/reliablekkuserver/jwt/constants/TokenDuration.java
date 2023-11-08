package com.deundeunhaku.reliablekkuserver.jwt.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenDuration {
  ACCESS_TOKEN_DURATION(30 * 60 * 1000L, 30 * 60, "30분"),
  REFRESH_TOKEN_DURATION(14 * 24 * 60 * 60 * 1000L, 14 * 24 * 60 * 60, "2주"),
  ACCESS_TOKEN_DURATION_ADMIN(12 * 30 * 24 * 60 * 60 * 1000L, 12 * 30 * 24 * 60 * 60, "1년"),
  ;

  private final long duration;
  private final int durationInSecond;
  private final String comment;
}
