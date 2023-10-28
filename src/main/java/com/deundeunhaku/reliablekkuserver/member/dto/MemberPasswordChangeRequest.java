package com.deundeunhaku.reliablekkuserver.member.dto;

public record MemberPasswordChangeRequest(

        String password
){

    public static MemberPasswordChangeRequest of(String password) {
        return new MemberPasswordChangeRequest(password);
    }
}

