package com.deundeunhaku.reliablekkuserver.order.dto;

public record RegisteredMenuRequest(
    Long menuId,
    Integer count
) {
  public static RegisteredMenuRequest of(Long menuId, Integer count) {
    return new RegisteredMenuRequest(menuId, count);
  }
}