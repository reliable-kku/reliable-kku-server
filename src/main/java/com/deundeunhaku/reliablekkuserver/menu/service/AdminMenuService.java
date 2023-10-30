package com.deundeunhaku.reliablekkuserver.menu.service;

import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import com.deundeunhaku.reliablekkuserver.menu.dto.AdminMenuChangeResponse;
import com.deundeunhaku.reliablekkuserver.menu.repository.AdminMenuRepository;
import com.deundeunhaku.reliablekkuserver.s3.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminMenuService {

  private final AdminMenuRepository adminMenuRepository;
  private final S3UploadService uploadService;


  @Transactional
  public AdminMenuChangeResponse changeSoldOut(Long menuId, boolean isSoldOut) {
    Menu menu = adminMenuRepository.findById(menuId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

    menu.setSale(isSoldOut);

    return AdminMenuChangeResponse.of(menu.getId(), menu.isSale());
  }

    @Transactional
    public void deleteMenu(Long menuId) {
      Menu menu = getMenuById(menuId);
      String imageUrl = uploadService.s3Bucket + menu.getMenuImageUrl();

      adminMenuRepository.deleteById(menuId);
      uploadService.deleteFile(imageUrl);
    }

  private Menu getMenuById(Long id) {
    return adminMenuRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("id와 일치하는 메뉴가 존재하지 않습니다."));
  }
}
