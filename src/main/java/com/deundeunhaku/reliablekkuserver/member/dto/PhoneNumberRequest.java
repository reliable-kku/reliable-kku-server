package com.deundeunhaku.reliablekkuserver.member.dto;

public record PhoneNumberRequest(
    String phoneNumber
) {

  public static PhoneNumberRequest of(String phoneNumber) {
    return new PhoneNumberRequest(phoneNumber);
  }

}
