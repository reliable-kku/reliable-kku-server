package com.deundeunhaku.reliablekkuserver.order.controller;


import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesResponse;
import com.deundeunhaku.reliablekkuserver.order.service.AdminOrderService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

class AdminOrderSalesControllerTest extends BaseControllerTest {

  @MockBean
  private AdminOrderService adminOrderService;

  @Test
  void 매출_내역의_리스트를_반환해준다() throws Exception {
      //given
    final LocalDate startDate = LocalDate.of(2021, 1, 1);
    final LocalDate endDate = LocalDate.of(2021, 1, 31);

    when(adminOrderService.getSalesBetween(startDate, endDate)).thenReturn(
        AdminSalesResponse.of(100, 12, 123, 1234, 123123, 123)
    );

      //when
    ResultActions resultActions = mockMvc.perform(get(API + "/admin/sales")
            .param("startDate", startDate.toString())
            .param("endDate", endDate.toString()))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("admin-sales",
            queryParameters(
                parameterWithName("startDate").description("조회 시작 날짜"),
                parameterWithName("endDate").description("조회 끝 날짜")
            ),
            responseFields(
                fieldWithPath("realAmount").description("실제 매출"),
                fieldWithPath("paymentCount").description("결제 건수"),
                fieldWithPath("avgPaymentAmount").description("평균 결제 금액"),
                fieldWithPath("refundAmount").description("환불매출"),
                fieldWithPath("refundCount").description("환불 건수"),
                fieldWithPath("avgRefundAmount").description("평균 환불 금액")
            )
        ));

  }

}