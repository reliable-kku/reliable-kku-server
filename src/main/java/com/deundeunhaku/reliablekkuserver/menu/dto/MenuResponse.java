package com.deundeunhaku.reliablekkuserver.menu.dto;

import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import java.util.List;

public record MenuResponse (
    Long menuId,
    String imageUrl,
    String menuName,
    Integer pricePerOne,
    Integer pricePerThree
) {
  public static MenuResponse of(Long menuId,String imageUrl, String menuName, Integer pricePerOne, Integer pricePerThree) {
    return new MenuResponse(menuId,imageUrl, menuName, pricePerOne, pricePerThree);
  }

  public static List<MenuResponse> listOf(List<Menu> menuList) {
    return menuList.stream()
        .map(menu -> MenuResponse.of(menu.getId(), menu.getMenuImageUrl(), menu.getName(), menu.getPricePerOne(), menu.getPricePerThree()))
        .toList();
  }
}
