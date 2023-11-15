package com.deundeunhaku.reliablekkuserver.menu.dto;

import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import java.util.List;

public record MenuResponse(
    Long menuId,
    String imageUrl,
    String menuName,
    String description,
    Integer pricePerOne,
    Integer pricePerThree,
    Boolean isSale
) {

  public static MenuResponse of(Long menuId, String imageUrl, String menuName, String description,
      Integer pricePerOne, Integer pricePerThree,Boolean isSale) {
    return new MenuResponse(menuId, imageUrl, menuName, description, pricePerOne, pricePerThree, isSale);
  }

  public static List<MenuResponse> listOf(List<Menu> menuList) {
    return menuList.stream()
        .map(menu -> MenuResponse.of(menu.getId(), menu.getMenuImageUrl(), menu.getName(),
            menu.getDescription(), menu.getPricePerOne(), menu.getPricePerThree(), menu.isSale()))
        .toList();
  }
}
