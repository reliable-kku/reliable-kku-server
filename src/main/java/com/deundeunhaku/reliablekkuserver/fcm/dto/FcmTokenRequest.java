package com.deundeunhaku.reliablekkuserver.fcm.dto;

public record FcmTokenRequest(
    String token
) {

  public static FcmTokenRequest of(String token) {
    return new FcmTokenRequest(token);
  }
}
