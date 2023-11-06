package com.deundeunhaku.reliablekkuserver.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AdminSalesEachTimeResponse{
    private final LocalTime eachTime;
    private final Integer totalPrice;

  public static AdminSalesEachTimeResponse of(LocalTime eachTime, Integer totalPrice) {
    return new AdminSalesEachTimeResponse(eachTime, totalPrice);
  }

  @QueryProjection
  public AdminSalesEachTimeResponse(LocalDateTime dateTime, Integer totalPrice) {
    this.eachTime = dateTime.toLocalTime();
    this.totalPrice = totalPrice;
  }
}
