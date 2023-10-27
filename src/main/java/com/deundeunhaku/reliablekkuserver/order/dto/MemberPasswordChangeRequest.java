package com.deundeunhaku.reliablekkuserver.order.dto;

public record MemberPasswordChangeRequest(
             String password
){

    public static MemberPasswordChangeRequest of(String password) {
        return new MemberPasswordChangeRequest(password);
    }
}

