package com.deundeunhaku.reliablekkuserver.order.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class PastOrderResponse {

  private final Integer day;
  private final String dayOfTheWeek;
  private final LocalTime orderedTime;
  private final List<OrderEachMenuResponse> orderMenuList;

  private PastOrderResponse(Integer day, String dayOfTheWeek, LocalTime orderedTime,
      List<OrderEachMenuResponse> orderMenuList) {
    this.day = day;
    this.dayOfTheWeek = dayOfTheWeek;
    this.orderedTime = orderedTime;
    this.orderMenuList = orderMenuList;
  }

  public static PastOrderResponse of(LocalDate orderedDate, LocalTime orderedTime,
      List<OrderEachMenuResponse> orderMenuList) {
    return new PastOrderResponse(
        orderedDate.getDayOfMonth(),
        orderedDate.getDayOfWeek().toString(),
        orderedTime,
        orderMenuList
    );
  }

}
