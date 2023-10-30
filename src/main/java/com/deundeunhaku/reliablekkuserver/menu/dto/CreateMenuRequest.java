package com.deundeunhaku.reliablekkuserver.menu.dto;

import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import lombok.Builder;

public record CreateMenuRequest(
        String name,
        Integer price,
        String description,
        String menuImageUrl

) {
    public static CreateMenuRequest of(String name, Integer price, String description, String menuImageUrl) {
        return new CreateMenuRequest(name, price, description, menuImageUrl);
    }
}
