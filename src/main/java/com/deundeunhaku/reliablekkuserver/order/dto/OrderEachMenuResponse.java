package com.deundeunhaku.reliablekkuserver.order.dto;

import com.querydsl.core.annotations.QueryProjection;

public record OrderEachMenuResponse(
    String name,
    Integer count
) {
  public static OrderEachMenuResponse of(String name, Integer count){
    return new OrderEachMenuResponse(name, count);
  }

  @QueryProjection
  public OrderEachMenuResponse(String name, Integer count) {
    this.name = name;
    this.count = count;
  }
}
