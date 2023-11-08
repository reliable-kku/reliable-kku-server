package com.deundeunhaku.reliablekkuserver.jwt.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenDuration {
//  FIXME : 임시로 10초로 설정
  ACCESS_TOKEN_DURATION(10 * 1000L, 30 * 60, "30분"),
  REFRESH_TOKEN_DURATION(14 * 24 * 60 * 60 * 1000L, 14 * 24 * 60 * 60, "2주"),
  ACCESS_TOKEN_DURATION_ADMIN(6 * 30 * 24 * 60 * 60 * 1000L, 6 * 30 * 24 * 60 * 60, "6개월"),
  ;

  private final long duration;
  private final int durationInSecond;
  private final String comment;
}
