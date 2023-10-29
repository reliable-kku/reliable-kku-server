package com.deundeunhaku.reliablekkuserver.menu.service;

import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import com.deundeunhaku.reliablekkuserver.menu.dto.AdminMenuChangeResponse;
import com.deundeunhaku.reliablekkuserver.menu.repository.AdminMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminMenuService {

  private final AdminMenuRepository adminMenuRepository;


  @Transactional
  public AdminMenuChangeResponse changeSoldOut(Long menuId, boolean isSoldOut) {
    Menu menu = adminMenuRepository.findById(menuId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

    menu.setSale(isSoldOut);

    return AdminMenuChangeResponse.of(menu.getId(), menu.isSale());
  }
}
