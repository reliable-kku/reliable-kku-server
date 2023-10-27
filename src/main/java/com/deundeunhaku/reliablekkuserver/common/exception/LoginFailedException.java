package com.deundeunhaku.reliablekkuserver.common.exception;

public class LoginFailedException extends RuntimeException {

    public LoginFailedException() {
        super("전화번호나 비밀번호가 일치하지 않습니다.");
    }

    public LoginFailedException(String message) {
        super(message);
    }
}
