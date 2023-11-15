package com.deundeunhaku.reliablekkuserver.order.dto;

import java.util.List;

public record OrderResponse(
    String username,
    Integer totalPrice,
    List<OrderEachMenuResponse> orderMenuList
) {
  public static OrderResponse of(String username,Integer totalPrice, List<OrderEachMenuResponse> orderMenuList){
    return new OrderResponse(username, totalPrice, orderMenuList);
  }

}
