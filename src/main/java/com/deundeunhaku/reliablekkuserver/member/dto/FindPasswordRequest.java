package com.deundeunhaku.reliablekkuserver.member.dto;

public record FindPasswordRequest(
    String phoneNumber,
    Integer certificationNumber
) {

  public static FindPasswordRequest of(String phoneNumber, Integer certificationNumber) {
    return new FindPasswordRequest(phoneNumber, certificationNumber);
  }
}
