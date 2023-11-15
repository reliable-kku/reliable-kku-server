package com.deundeunhaku.reliablekkuserver.menu.dto;

public record AdminMenuChangeResponse(
    Long menuId,
    Boolean isSoldOut
) {

  public static AdminMenuChangeResponse of(Long menuId, Boolean isSoldOut) {
    return new AdminMenuChangeResponse(menuId, isSoldOut);
  }
}
