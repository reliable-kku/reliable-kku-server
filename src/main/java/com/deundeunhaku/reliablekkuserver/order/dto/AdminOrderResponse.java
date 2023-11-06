package com.deundeunhaku.reliablekkuserver.order.dto;

import java.time.LocalTime;
import java.util.List;

public record AdminOrderResponse(
    Long orderId,
    Long todayOrderCount,
    String phoneNumber,
    LocalTime orderTime,
    Boolean isOfflineOrder,
    long timeTakenMinutes,
    int allCount,
    List<OrderEachMenuResponse> menuResponse
) {

  public static AdminOrderResponse of(Long orderId, Long todayOrderCount, String phoneNumber,
      LocalTime orderTime,
      Boolean isOfflineOrder, long timeTakenMinutes, int allCount,
      List<OrderEachMenuResponse> menuResponse) {
    return new AdminOrderResponse(orderId, todayOrderCount, phoneNumber, orderTime, isOfflineOrder,
        timeTakenMinutes, allCount,
        menuResponse);
  }
}
