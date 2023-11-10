package com.deundeunhaku.reliablekkuserver.order.controller;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminOrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachCountResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachMenuResponse;
import com.deundeunhaku.reliablekkuserver.order.service.AdminOrderService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

class AdminOrderControllerTest extends BaseControllerTest {

  @MockBean
  private AdminOrderService adminOrderService;



  @Test
  void 관리자가_볼_주문리스트를_반환한다() throws Exception {
    //given
    String orderStatus = OrderStatus.COOKING.name();

    OrderEachMenuResponse eachOrderResponse1 = OrderEachMenuResponse.of("후라이드", 1);
    OrderEachMenuResponse eachOrderResponse2 = OrderEachMenuResponse.of("양념", 2);
    OrderEachMenuResponse eachOrderResponse3 = OrderEachMenuResponse.of("간장", 3);

    AdminOrderResponse adminOrderResponse1 = AdminOrderResponse.of(
        1L,
        1L,
        "01012341234",
        LocalTime.of(12, 30, 30),
        true,
        10L,
        100,
        List.of(eachOrderResponse1, eachOrderResponse2, eachOrderResponse3)
    );

    AdminOrderResponse adminOrderResponse2 = AdminOrderResponse.of(
        2L,
        2L,
        "01011111111",
        LocalTime.of(12, 11, 30),
        false,
        10L,
        100,
        List.of(eachOrderResponse1, eachOrderResponse2, eachOrderResponse3)
    );

    AdminOrderResponse adminOrderResponse3 = AdminOrderResponse.of(
        3L,
        3L,
        "01022222222",
        LocalTime.of(9, 12, 30),
        true,
        10L,
        10,
        List.of(eachOrderResponse1, eachOrderResponse2, eachOrderResponse3)
    );

    List<AdminOrderResponse> response = List.of(adminOrderResponse1,
        adminOrderResponse2, adminOrderResponse3);

    when(adminOrderService.getOrderList(OrderStatus.COOKING))
        .thenReturn(response);
    //when
    ResultActions resultActions = mockMvc.perform(get(API + "/admin/orders")
            .param("orderStatus", orderStatus))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("admin/order-list/success",
            queryParameters(
                parameterWithName("orderStatus").description("주문 상태")
            ),
            responseFields(
                fieldWithPath("[].orderId").description("order의 id 값"),
                fieldWithPath("[].todayOrderCount").description("주문 번호"),
                fieldWithPath("[].phoneNumber").description("주문자 전화번호"),
                fieldWithPath("[].orderTime").description("주문 시간"),
                fieldWithPath("[].menuResponse").description("주문 메뉴"),
                fieldWithPath("[].isOfflineOrder").description("오프라인 주문 여부"),
                fieldWithPath("[].timeTakenMinutes").description("주문 예상 걸리는 시간"),
                fieldWithPath("[].allCount").description("주문당 마리수"),
                fieldWithPath("[].menuResponse[].name").description("메뉴 이름"),
                fieldWithPath("[].menuResponse[].count").description("메뉴 개수")
            )
        ));
  }

  @Test
  void 대기중인_주문을_조리중으로_변경하고_시간을_보낸다() throws Exception {
    //given
    Long orderId = 1L;
    Integer orderMinutes = 10;

    //when
    ResultActions resultActions = mockMvc.perform(
            post(API + "/admin/orders/{orderId}/minutes/{orderMinutes}", orderId, orderMinutes))
        .andDo(print());

    //then
    resultActions.andExpect(status().isCreated())
        .andDo(document("admin/order-status/success",
            pathParameters(
                parameterWithName("orderId").description("주문 상태"),
                parameterWithName("orderMinutes").description("주문 예상 걸리는 시간")
            )
        ));

  }

  @Test
  void 관리자가_주문을_취소한다() throws Exception {
      //given
    final Long orderId = 1L;

      //when
    ResultActions resultActions = mockMvc.perform(delete(API + "/admin/orders/{orderId}", orderId))
        .andDo(print());

    //then
    resultActions.andExpect(status().isNoContent())
        .andDo(document("admin/order-cancel/success",
            pathParameters(
                parameterWithName("orderId").description("주문 번호")
            )));
  }

  @Test
  void 주문을_제작완료하면_픽업으로_상태를_변경한다() throws Exception {
    //given
    Long orderId = 1L;

    //when
    ResultActions resultActions = mockMvc.perform(
            patch(API + "/admin/orders/{orderId}/pick-up", orderId))
        .andDo(print());

    //then
    resultActions.andExpect(status().isNoContent())
        .andDo(document("admin/order-pick-up/success",
            pathParameters(
                parameterWithName("orderId").description("주문 번호")
            )));

  }

  @Test
  void 사용자가_음식을_수령하면_관리자가_완료를_누른다() throws Exception {
    //given
    Long orderId = 1L;

    //when
    ResultActions resultActions = mockMvc.perform(
            patch(API + "/admin/orders/{orderId}/finish", orderId))
        .andDo(print());

    //then
    resultActions.andExpect(status().isNoContent())
        .andDo(document("admin/order-finish/success",
            pathParameters(
                parameterWithName("orderId").description("주문 번호")
            )));
  }

  @Test
  void 사용자가_음식을_미수령하면_미수령처리한다() throws Exception {
    //given
    Long orderId = 1L;

    //when
    ResultActions resultActions = mockMvc.perform(
            patch(API + "/admin/orders/{orderId}/not-take", orderId))
        .andDo(print());

    //then
    resultActions.andExpect(status().isNoContent())
        .andDo(document("admin/order-not-take/success",
            pathParameters(
                parameterWithName("orderId").description("주문 번호")
            )));
  }

  @Test
  void order_상태별_주문개수를_반환한다() throws Exception {
      //given
    LocalDate currentDate = LocalDate.of(2023, 11, 1);
    OrderEachCountResponse response = OrderEachCountResponse.of(1, 2, 3);
    when(adminOrderService.getOrderCount(currentDate)).thenReturn(response);
      //when
    ResultActions resultActions = mockMvc.perform(
            get(API + "/admin/orders/orderCount")
                    .param("currentDate", String.valueOf(currentDate)))
                    .andDo(print());

      //then
    resultActions.andExpect(status().isOk()).andDo(
            document("admin/orders/orderCount",
                    queryParameters(
                            parameterWithName("currentDate").description("당일 주문 날짜")
                    ),
                    responseFields(
                            fieldWithPath("waitOrderCount").description("대기 상태의 주문 개수"),
                            fieldWithPath("cookingOrderCount").description("접수 상태의 주문 개수"),
                            fieldWithPath("finishOrderCount").description("완료 상태의 주문 개수")
                            )
                    )
    );
  }

}