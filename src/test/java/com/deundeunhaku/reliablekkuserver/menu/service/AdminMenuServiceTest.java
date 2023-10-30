package com.deundeunhaku.reliablekkuserver.menu.service;

import com.deundeunhaku.reliablekkuserver.BaseServiceTest;
import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import com.deundeunhaku.reliablekkuserver.menu.repository.AdminMenuRepository;
import com.deundeunhaku.reliablekkuserver.s3.service.S3UploadService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Mockito.*;

class AdminMenuServiceTest extends BaseServiceTest {

    @InjectMocks
    AdminMenuService adminMenuService;

    @Mock
    AdminMenuRepository adminMenuRepository;

    @Mock
    S3UploadService s3UploadService;
    @Test
    void 메뉴_삭제에_성공한다() {
        //given
        Long menuId = 1L;
        Menu menu = Menu.builder().id(1L).build();

        when(adminMenuRepository.findById(anyLong())).thenReturn(Optional.of(menu));
        //when
        adminMenuService.deleteMenu(menu.getId());
        //then
        verify(adminMenuRepository, times(1)).findById(menuId);
        verify(adminMenuRepository, times(1)).deleteById(menuId);
    }
}