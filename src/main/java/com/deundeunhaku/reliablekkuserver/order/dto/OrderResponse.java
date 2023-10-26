package com.deundeunhaku.reliablekkuserver.order.dto;

import java.util.List;

public record OrderResponse(
    Integer totalPrice,
    List<MenuResponse> orderMenuList
) {
  public static OrderResponse of(Integer totalPrice, List<MenuResponse> orderMenuList){
    return new OrderResponse(totalPrice, orderMenuList);
  }

}
