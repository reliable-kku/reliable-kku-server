package com.deundeunhaku.reliablekkuserver.order.dto;

import java.util.List;

public record OrderResponse(
    Integer totalPrice,
    List<OrderEachMenuResponse> orderMenuList
) {
  public static OrderResponse of(Integer totalPrice, List<OrderEachMenuResponse> orderMenuList){
    return new OrderResponse(totalPrice, orderMenuList);
  }

}
