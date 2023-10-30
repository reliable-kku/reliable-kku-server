package com.deundeunhaku.reliablekkuserver.menu.service;

import com.deundeunhaku.reliablekkuserver.BaseServiceTest;
import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import com.deundeunhaku.reliablekkuserver.menu.dto.CreateMenuRequest;
import com.deundeunhaku.reliablekkuserver.menu.dto.CreateMenuResponse;
import com.deundeunhaku.reliablekkuserver.menu.repository.AdminMenuRepository;
import com.deundeunhaku.reliablekkuserver.s3.dto.S3Response;
import com.deundeunhaku.reliablekkuserver.s3.service.S3UploadService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdminCreateMenuServiceTest extends BaseServiceTest {

    @InjectMocks
    AdminCreateMenuService adminCreateMenuService;
    @Mock
    AdminMenuRepository adminMenuRepository;
    @Mock
    S3UploadService uploadService;
    @Test
    void 메뉴를_만들고_메뉴_이미지를_저장한다(){
        //given
        MultipartFile multipartFile = new MockMultipartFile("file", "fileOriginName", "image/jpeg", "file".getBytes());

        CreateMenuRequest request = CreateMenuRequest.of(
                "사과잼 와플",
                5000,
                "맛있는 사과잼 와플이 왔어용~",
                "imageUrl"
        );
        Long menuId = 1L;
        when(adminMenuRepository.save(any())).thenReturn(Menu.builder().id(menuId).build());

        when(uploadService.saveFileWithUUID(any()))
                .thenReturn(S3Response.of("imageName", "fileS3Key", "fileOriginName"));


        //when
        CreateMenuResponse response = adminCreateMenuService.creatAndCreateFile(multipartFile, request);
        //then
        assertThat(response.menuId()).isEqualTo(menuId);
    }

}