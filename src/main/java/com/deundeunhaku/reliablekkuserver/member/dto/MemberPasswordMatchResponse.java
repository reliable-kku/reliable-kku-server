package com.deundeunhaku.reliablekkuserver.member.dto;

public record MemberPasswordMatchResponse(
    Boolean isPasswordMatch
){

        public static MemberPasswordMatchResponse of(Boolean currentPassword) {
            return new MemberPasswordMatchResponse(currentPassword);
        }
    }
