package com.deundeunhaku.reliablekkuserver.order.dto;

public record MenuResponse(
    String name,
    Integer count
) {
  public static MenuResponse of(String name, Integer count){
    return new MenuResponse(name, count);
  }
}
