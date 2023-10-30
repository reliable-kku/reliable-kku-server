package com.deundeunhaku.reliablekkuserver.menu.controller;

import com.deundeunhaku.reliablekkuserver.menu.dto.AdminMenuChangeResponse;
import com.deundeunhaku.reliablekkuserver.menu.dto.MenuResponse;
import com.deundeunhaku.reliablekkuserver.menu.service.AdminMenuService;
import com.deundeunhaku.reliablekkuserver.menu.service.MenuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/menu")
public class AdminMenuController {

  private final MenuService menuService;
  private final AdminMenuService adminMenuService;

  @GetMapping
  public ResponseEntity<List<MenuResponse>> getMenuList() {
    return ResponseEntity.ok(menuService.getMenuList());
  }

  @PatchMapping("/{menuId}")
  public ResponseEntity<AdminMenuChangeResponse> updateMenu(@PathVariable Long menuId,
      @RequestParam boolean isSoldOut) {
    return ResponseEntity.ok(adminMenuService.changeSoldOut(menuId,
        isSoldOut));
  }

  @DeleteMapping("/{menuId}")
  public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId){

    adminMenuService.deleteMenu(menuId);

    return ResponseEntity.noContent().build();
  }
}
