package com.deundeunhaku.reliablekkuserver.member.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
    @NotNull String phoneNumber,
    @NotNull String password
) {

  public static LoginRequest of(String phoneNumber, String password) {
    return new LoginRequest(phoneNumber, password);
  }

}
