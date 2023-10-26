package com.deundeunhaku.reliablekkuserver.menu.controller;


import com.deundeunhaku.reliablekkuserver.menu.dto.MenuResponse;
import com.deundeunhaku.reliablekkuserver.menu.service.MenuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

  private final MenuService menuService;

  @GetMapping
  public ResponseEntity<List<MenuResponse>> getMenuList() {
    return ResponseEntity.ok(menuService.getMenuList());
  }
}
