package com.deundeunhaku.reliablekkuserver.member.dto;

public record MemberMyPageResponse(
        String realName,
        Integer level
) {
    public static MemberMyPageResponse of(String realName, Integer Level){
        return new MemberMyPageResponse(realName, Level);
    }
}
