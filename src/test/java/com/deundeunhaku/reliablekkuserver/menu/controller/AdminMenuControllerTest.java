package com.deundeunhaku.reliablekkuserver.menu.controller;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.menu.dto.AdminMenuChangeResponse;
import com.deundeunhaku.reliablekkuserver.menu.dto.MenuResponse;
import com.deundeunhaku.reliablekkuserver.menu.service.AdminMenuService;
import com.deundeunhaku.reliablekkuserver.menu.service.MenuService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminMenuControllerTest extends BaseControllerTest {

  @MockBean
  private MenuService menuService;

  @MockBean
  private AdminMenuService adminMenuService;

  @Test
  void 메뉴의_리스트를_반환한다() throws Exception {
    //given
    MenuResponse menuResponse1 = MenuResponse.of(1L, "imageUrl1", "든붕이", "든붕이임", 1000, 3000);
    MenuResponse menuResponse2 = MenuResponse.of(2L, "imageUrl2", "팥붕이", "팥붕이야", 2000, 5000);
    MenuResponse menuResponse3 = MenuResponse.of(3L, "imageUrl3", "누붕이", "누붕이일까", 3000, 7000);

    when(menuService.getMenuList())
        .thenReturn(List.of(menuResponse1, menuResponse2, menuResponse3));
    //when
    ResultActions resultActions = mockMvc.perform(get(API + "/admin/menu"))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("admin/menu/list/success",
            responseFields(
                fieldWithPath("[].menuId").description("메뉴의 ID"),
                fieldWithPath("[].imageUrl").description("메뉴의 이미지 URL"),
                fieldWithPath("[].menuName").description("메뉴의 이름"),
                fieldWithPath("[].description").description("메뉴 설명"),
                fieldWithPath("[].pricePerOne").description("메뉴의 가격"),
                fieldWithPath("[].pricePerThree").description("메뉴의 할인된 가격")
            )));
  }

  @Test
  void 메뉴의_품절_품절헤제를_수정한다() throws Exception {
    //given
    long menuId = 1L;
    boolean isSoldOut = true;
    AdminMenuChangeResponse response = AdminMenuChangeResponse.of(menuId, isSoldOut);

    when(adminMenuService.changeSoldOut(menuId, isSoldOut))
        .thenReturn(response);

    //when
    ResultActions resultActions = mockMvc.perform(patch(API + "/admin/menu/{menuId}", menuId)
            .queryParam("isSoldOut", String.valueOf(isSoldOut))
            .contentType(APPLICATION_JSON)
        )
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("admin/menu/change/success",
            pathParameters(
                parameterWithName("menuId").description("메뉴의 ID")
            ),
            queryParameters(
                parameterWithName("isSoldOut").description("메뉴의 품절 여부")
            ),
            responseFields(
                fieldWithPath("menuId").description("메뉴의 ID"),
                fieldWithPath("isSoldOut").description("메뉴의 품절 여부")
            )));
  }
  
  @Test
  void 메뉴를_삭제한다() throws Exception {
    //given
    long menuId = 1L;
    //when
    ResultActions resultActions = mockMvc.perform(delete(API + "/admin/menu/{menuId}", menuId)
            )
            .andDo(print());
    //then
    resultActions.andExpect(status().isNoContent())
            .andDo(document("admin/menu/delete/success",
                    pathParameters(
                            parameterWithName("menuId").description("메뉴의 ID")
                    )
                    ));
  }
}