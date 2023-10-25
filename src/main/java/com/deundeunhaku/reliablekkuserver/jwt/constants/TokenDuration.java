package com.deundeunhaku.reliablekkuserver.jwt.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenDuration {
    ACCESS_TOKEN_DURATION(30 * 60 * 1000L, "30분"),
    REFRESH_TOKEN_DURATION(14 * 24 * 60 * 60 * 1000L, "2주"),
    ;

    private final long duration;
    private final String comment;
}
