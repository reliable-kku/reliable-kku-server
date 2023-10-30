package com.deundeunhaku.reliablekkuserver.menu.controller;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.menu.dto.CreateMenuRequest;
import com.deundeunhaku.reliablekkuserver.menu.dto.CreateMenuResponse;
import com.deundeunhaku.reliablekkuserver.menu.service.AdminCreateMenuService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminCreateMenuControllerTest extends BaseControllerTest {

    @Mock
    AdminCreateMenuService adminCreateMenuService;

    @Test
    void 메뉴를_등록한다() throws Exception {
        //given
        final CreateMenuResponse response = CreateMenuResponse.of(1L);
        MockMultipartFile file1 = new MockMultipartFile("file", "file1.png", MediaType.MULTIPART_FORM_DATA_VALUE, "file1".getBytes(UTF_8));
        final CreateMenuRequest request = CreateMenuRequest.of(
                "사과잼와플",
                4900,
                "맛있는 사과잼 와플이 왔어용~",
                "imageUrl"
        );
        MockMultipartFile menu = new MockMultipartFile("menu", null, MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(request).getBytes(UTF_8));

        when(adminCreateMenuService.creatAndCreateFile(any(), any(CreateMenuRequest.class))).thenReturn(response);

        //when
        ResultActions resultActions = mockMvc.perform(multipart(API + "/admin/create-menu")
                .file(file1)
                .file(menu)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isCreated())
                .andDo(document("admin/create-menu/success",
                                requestParts(
                                        partWithName("file").description("이미지 파일"),
                                        partWithName("menu").description("메뉴 정보")
                                ),
                                requestPartFields(
                                        "menu",
                                        fieldWithPath("name").description("메뉴 이름"),
                                        fieldWithPath("price").description("메뉴 가격"),
                                        fieldWithPath("description").description("메뉴 설명"),
                                        fieldWithPath("menuImageUrl").description("메뉴 이미지 경로")
                                ),
                                responseFields(
                                        fieldWithPath("menuId").description("메뉴 id")
                                )
                        )
                );
    }


}