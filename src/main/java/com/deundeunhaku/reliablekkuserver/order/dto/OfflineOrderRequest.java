package com.deundeunhaku.reliablekkuserver.order.dto;

import java.util.List;

public record OfflineOrderRequest(
    String phoneNumber,
    int totalPrice,
    List<RegisteredMenuRequest> registeredMenus

) {

  public static OfflineOrderRequest of(String phoneNumber, int totalPrice,
      List<RegisteredMenuRequest> registeredMenus) {
    return new OfflineOrderRequest(phoneNumber, totalPrice, registeredMenus);
  }
}
