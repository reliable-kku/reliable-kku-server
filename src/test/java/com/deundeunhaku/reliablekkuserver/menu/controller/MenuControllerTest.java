package com.deundeunhaku.reliablekkuserver.menu.controller;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.menu.dto.MenuResponse;
import com.deundeunhaku.reliablekkuserver.menu.service.MenuService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.ResultActions;

class MenuControllerTest extends BaseControllerTest {

  @MockBean
  private MenuService menuService;

  @Test
  void 메뉴의_리스트를_반환한다() throws Exception {
      //given
    MenuResponse menuResponse1 = MenuResponse.of(1L,"imageUrl1", "든붕이", "든붕이임",1000, 3000);
    MenuResponse menuResponse2 = MenuResponse.of(2L,"imageUrl2", "팥붕이","팥붕이야", 2000, 5000);
    MenuResponse menuResponse3 = MenuResponse.of(3L, "imageUrl3", "누붕이","누붕이일까", 3000, 7000);

    when(menuService.getMenuList())
        .thenReturn(List.of(menuResponse1, menuResponse2, menuResponse3));
    //when
    ResultActions resultActions = mockMvc.perform(get(API + "/menu"))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(MockMvcRestDocumentation.document("menu/list/success",
            responseFields(
                fieldWithPath("[].menuId").description("메뉴의 ID"),
                fieldWithPath("[].imageUrl").description("메뉴의 이미지 URL"),
                fieldWithPath("[].menuName").description("메뉴의 이름"),
                fieldWithPath("[].description").description("메뉴 설명"),
                fieldWithPath("[].pricePerOne").description("메뉴의 가격"),
                fieldWithPath("[].pricePerThree").description("메뉴의 할인된 가격")
            )));

  }

}