package com.deundeunhaku.reliablekkuserver.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalTime;
import java.util.List;

public record AdminOrderResponse(
    Long todayOrderCount,
    String phoneNumber,
    LocalTime orderTime,
    Boolean isOfflineOrder,
    List<OrderEachMenuResponse> menuResponse
) {

  public static AdminOrderResponse of(Long todayOrderCount, String phoneNumber, LocalTime orderTime,
      Boolean isOfflineOrder, List<OrderEachMenuResponse> menuResponse) {
    return new AdminOrderResponse(todayOrderCount, phoneNumber, orderTime, isOfflineOrder,
        menuResponse);
  }
}
