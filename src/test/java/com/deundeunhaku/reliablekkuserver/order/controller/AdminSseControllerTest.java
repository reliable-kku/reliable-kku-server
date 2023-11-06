package com.deundeunhaku.reliablekkuserver.order.controller;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.order.service.AdminOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class AdminSseControllerTest extends BaseControllerTest {

  @MockBean
  private AdminOrderService orderService;

  @Test
  void 관리자가_sse로_데이터를_받아온다() throws Exception {
    //given
    SseEmitter sseEmitter = new SseEmitter();
    when(orderService.connectSse()).thenReturn(sseEmitter);

    //when
    ResultActions resultActions = mockMvc.perform(get("/api/v1/admin/order/sse/connect"))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("admin-sse-connect"));
  }

}