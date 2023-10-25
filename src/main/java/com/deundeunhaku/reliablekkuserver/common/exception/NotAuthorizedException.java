package com.deundeunhaku.reliablekkuserver.common.exception;

public class NotAuthorizedException extends RuntimeException {

    public NotAuthorizedException() {
        super("인증되지 않은 토큰입니다.");
    }

    public NotAuthorizedException(String message) {
        super(message);
    }
}
