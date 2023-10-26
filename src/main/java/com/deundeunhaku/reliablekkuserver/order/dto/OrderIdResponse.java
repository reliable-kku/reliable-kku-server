package com.deundeunhaku.reliablekkuserver.order.dto;

public record OrderIdResponse(
    Long id
) {
  public static OrderIdResponse of(Long id) {

    return new OrderIdResponse(id);
  }
}
