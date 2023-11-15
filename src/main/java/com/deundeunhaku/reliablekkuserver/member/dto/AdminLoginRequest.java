package com.deundeunhaku.reliablekkuserver.member.dto;

public record AdminLoginRequest (
    String username,
    String password
) {
  public static AdminLoginRequest of(String username, String password) {
    return new AdminLoginRequest(username, password);
  }
}
