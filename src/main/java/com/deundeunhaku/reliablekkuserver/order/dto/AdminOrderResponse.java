package com.deundeunhaku.reliablekkuserver.order.dto;

import java.time.LocalTime;
import java.util.List;

public record AdminOrderResponse(
    Long todayOrderCount,
    String phoneNumber,
    LocalTime orderTime,
    Boolean isOfflineOrder,
    long timeTakenMinutes,
    int allCount,
    List<OrderEachMenuResponse> menuResponse
) {

  public static AdminOrderResponse of(Long todayOrderCount, String phoneNumber, LocalTime orderTime,
      Boolean isOfflineOrder, long timeTakenMinutes, int allCount,
      List<OrderEachMenuResponse> menuResponse) {
    return new AdminOrderResponse(todayOrderCount, phoneNumber, orderTime, isOfflineOrder,
        timeTakenMinutes, allCount,
        menuResponse);
  }
}
