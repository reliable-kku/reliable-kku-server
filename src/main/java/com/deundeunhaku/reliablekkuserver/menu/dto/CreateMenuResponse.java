package com.deundeunhaku.reliablekkuserver.menu.dto;

public record CreateMenuResponse(
        Long menuId
) {
    public static CreateMenuResponse of(Long menuId){
        return new CreateMenuResponse(menuId);
    }
}
