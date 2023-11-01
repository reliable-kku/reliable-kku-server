package com.deundeunhaku.reliablekkuserver.order.dto;

public record LeftTimeResponse(
    Long leftMinutes
) {

  public static LeftTimeResponse of(Long leftMinutes) {
    return new LeftTimeResponse(leftMinutes);
  }

}
