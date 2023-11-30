package com.deundeunhaku.reliablekkuserver.order.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PastOrderResponse {

  private final Integer day;
  private final String dayOfTheWeek;
  private final LocalTime orderedTime;
  private final Integer totalPrice;
  private final Integer totalCount;
  private final List<OrderEachMenuResponse> orderMenuList;


  public static PastOrderResponse of(LocalDate orderedDate, LocalTime orderedTime,
      Integer totalPrice,
      List<OrderEachMenuResponse> orderMenuList) {
    return new PastOrderResponse(
        orderedDate.getDayOfMonth(),
        orderedDate.getDayOfWeek().toString(),
        orderedTime,
        totalPrice,
        orderMenuList.stream().mapToInt(OrderEachMenuResponse::count).sum(),
        orderMenuList
    );
  }

  public static PastOrderResponse of(ZonedDateTime orderedDatetime,
      Integer totalPrice,
      List<OrderEachMenuResponse> orderMenuList) {
    return new PastOrderResponse(
        orderedDatetime.getDayOfMonth(),
        orderedDatetime.getDayOfWeek().toString(),
        LocalTime.of(orderedDatetime.getHour(), orderedDatetime.getMinute()),
        totalPrice,
        orderMenuList.stream().mapToInt(OrderEachMenuResponse::count).sum(),
        orderMenuList
    );
  }

}
