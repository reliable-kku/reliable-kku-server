package com.deundeunhaku.reliablekkuserver.order.dto;

public record MemberPasswordMatchResponse(
        boolean isPasswordMatch
){

    public static MemberPasswordMatchResponse of(boolean currentPassword) {
        return new MemberPasswordMatchResponse(currentPassword);
    }
}

