package com.deundeunhaku.reliablekkuserver.order.dto;

import jakarta.validation.constraints.Min;
import java.util.List;

public record OrderRegisterRequest(
    String tossOrderId,
    @Min(value = 0, message = "주문 금액은 0보다 작을 수 없습니다")
    int orderPrice,

    List<RegisteredMenuRequest> registeredMenus
) {
  public static OrderRegisterRequest of(String tossOrderId, int orderPrice, List<RegisteredMenuRequest> registeredMenus) {
    return new OrderRegisterRequest(tossOrderId, orderPrice, registeredMenus);
  }

}


