package com.deundeunhaku.reliablekkuserver.member.dto;

public record MemberRegisterRequest(
    String realName,
    String phoneNumber,
    String password
) {
  public static MemberRegisterRequest of(String realName, String phoneNumber, String password) {
    return new MemberRegisterRequest(realName, phoneNumber, password);
  }
}