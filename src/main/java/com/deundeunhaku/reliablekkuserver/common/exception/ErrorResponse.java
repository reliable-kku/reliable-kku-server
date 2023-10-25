package com.deundeunhaku.reliablekkuserver.common.exception;

import lombok.Getter;


@Getter
public class ErrorResponse {

    private final String message;

    private ErrorResponse(String message) {
        this.message = message;
    }

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }
}
