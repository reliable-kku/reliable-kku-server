package com.deundeunhaku.reliablekkuserver.store.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.store.constant.STORE_ID;
import com.deundeunhaku.reliablekkuserver.store.domain.Store;
import com.deundeunhaku.reliablekkuserver.store.service.StoreService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

class AdminStoreControllerTest extends BaseControllerTest {

  @MockBean
  private StoreService storeService;

  @Test
  void 가게가_영업중인지_아닌지_반환한다() throws Exception {
    //given

    when(storeService.getStoreOpenOrClosed(any(STORE_ID.class)))
        .thenReturn(Store.builder().id(1L).isOpened(true).build());
    //when

    ResultActions resultActions = mockMvc.perform(get(API + "/admin/stores/open-closed"))
        .andDo(print());
    //then
    resultActions
        .andExpect(status().isOk())
        .andDo(document("admin/store/get/open-closed",
            responseFields(
                fieldWithPath("id").description("store의 id"),
                fieldWithPath("isOpened").description("가게의 영업 여부")
            )));
  }

  @Test
  void 가게를_영업종료하고_시작한다() throws Exception {
      //given

    when(storeService.setStoreOpenOrClosed(any(STORE_ID.class)))
        .thenReturn(Store.builder().id(1L).isOpened(false).build());
      //when

    ResultActions resultActions = mockMvc.perform(put(API + "/admin/stores/open-closed"))
        .andDo(print());
    //then
    resultActions
        .andExpect(status().isOk())
        .andDo(document("admin/store/put/open-closed",
            responseFields(
                fieldWithPath("id").description("store의 id"),
                fieldWithPath("isOpened").description("가게의 영업 여부")
            )));
  }

}