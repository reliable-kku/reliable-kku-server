package com.deundeunhaku.reliablekkuserver.common.dto;

public record BaseMessageResponse(
        String message
) {
    public static BaseMessageResponse of(String message) {
        return new BaseMessageResponse(message);
    }
}
