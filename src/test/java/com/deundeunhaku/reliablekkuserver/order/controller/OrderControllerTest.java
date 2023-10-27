package com.deundeunhaku.reliablekkuserver.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachMenuResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderIdResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderRegisterRequest;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.RegisteredMenuRequest;
import com.deundeunhaku.reliablekkuserver.order.service.OrderService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

class OrderControllerTest extends BaseControllerTest {

  @MockBean
  private OrderService orderService;

  @Test
  void 유저의_주문을_등록한다() throws Exception {
      //given
    RegisteredMenuRequest menu1 = RegisteredMenuRequest.of(
        1L,
        3
    );
    RegisteredMenuRequest menu2 = RegisteredMenuRequest.of(
        2L,
        5
    );

    OrderRegisterRequest request = OrderRegisterRequest.of(
        "tossOrderId",
        10000,
        List.of(menu1, menu2)
    );

    when(orderService.registerOrder(any(), any()))
        .thenReturn(
            OrderIdResponse.of(1L)
        );

    //when
    ResultActions resultActions = mockMvc.perform(post(API + "/order")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print());

    //then
    resultActions.andExpect(status().isCreated())
        .andDo(document("order/register/success",
            requestFields(
                fieldWithPath("tossOrderId").description("토스 주문번호"),
                fieldWithPath("orderPrice").description("총 가격"),
                fieldWithPath("registeredMenus[].menuId")
                    .description("각 메뉴 아이디"),
                fieldWithPath("registeredMenus[].count").description("각 메뉴 개수")
            ),
            responseFields(
                fieldWithPath("id").description("주문 아이디")
            )
        ));
  }

  @Test
  void 주문의_메뉴_리스트를_반환한다() throws Exception {
    //given
    Long orderId = 1L;

    OrderEachMenuResponse 팥_붕어빵 = OrderEachMenuResponse.of("팥 붕어빵", 2);
    OrderEachMenuResponse 슈크림_붕어빵 = OrderEachMenuResponse.of("슈크림 붕어빵", 4);

    when(orderService.getOrderMenuList(orderId))
        .thenReturn(OrderResponse.of(15000, List.of(팥_붕어빵, 슈크림_붕어빵)));

    //when
    ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(API + "/order/{orderId}", orderId))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("order/get/success",
            pathParameters(
                parameterWithName("orderId").description("주문 ID")
            ),
            responseFields(
                fieldWithPath("totalPrice").description("총 가격"),
                fieldWithPath("orderMenuList[].name").description("메뉴 이름"),
                fieldWithPath("orderMenuList[].count").description("메뉴 개수")
            )
        ));

  }

  @Test
  void 주문을_취소한다() throws Exception {
      //given
    Long orderId = 1L;

    //when
    ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.delete(API + "/order/{orderId}", orderId))
        .andDo(print());

      //then
    resultActions.andExpect(status().isNoContent())
        .andDo(document("order/delete/success",
            pathParameters(
                parameterWithName("orderId").description("주문 ID")
            )
        ));
  }

  @Test
  void 주문이_현재_진행중인지_확인해준다() throws Exception {
      //given
      //when
    ResultActions resultActions = mockMvc.perform(get(API + "/order/duplicate"))
        .andDo(print());

    //then
    resultActions.andExpect(status().isNoContent())
        .andDo(document("order/duplicate/success"));

  }

}