package com.deundeunhaku.reliablekkuserver.member.dto;

public record LoginRequest(
    String phoneNumber,
    String password
) {

}
