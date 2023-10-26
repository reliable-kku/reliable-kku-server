package com.deundeunhaku.reliablekkuserver.order.controller;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class OrderSseControllerTest extends BaseControllerTest {

  @MockBean
  private OrderService orderService;

  @Test
  void 주문시간_상태를_반환하는_sse를_연결한다() throws Exception {
    //given
    Long orderId = 1L;

    when(orderService.connect(orderId))
        .thenReturn(new SseEmitter());
    //when
    ResultActions resultActions = mockMvc.perform(get("/api/v1/order/sse/connect")
            .accept(MediaType.TEXT_EVENT_STREAM_VALUE)
            .param("orderId", orderId.toString()))
        .andDo(print());
    //then

    resultActions.andExpect(status().isOk())
        .andDo(document("order/sse/connect/success",
            queryParameters(
                parameterWithName("orderId").description("연결할 주문의 id")
            )
        ));


  }

}