package com.deundeunhaku.reliablekkuserver.order.controller;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesCalendarResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.TotalSalesMonthOfDay;
import com.deundeunhaku.reliablekkuserver.order.service.AdminOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AdminOrderSalesCalendarControllerTest extends BaseControllerTest {

    @MockBean
    private AdminOrderService adminOrderService;
    @Test
    void 매출관리_달력의_데이터를_나타낸다() throws Exception {
        //given
        LocalDate date = LocalDate.of(2023, 11, 6);
        TotalSalesMonthOfDay monthOfDay1 = TotalSalesMonthOfDay.of(3500, 700);
        TotalSalesMonthOfDay monthOfDay2 = TotalSalesMonthOfDay.of(3500, 700);
        TotalSalesMonthOfDay monthOfDay3 = TotalSalesMonthOfDay.of(3500, 700);

        when(adminOrderService.getSalesCalendar(date)).thenReturn(
                AdminSalesCalendarResponse.of(10, 100000, 10000, List.of(monthOfDay1, monthOfDay2, monthOfDay3))
        );
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/admin/sales/calendar")
                .param("date", date.toString()))
                .andDo(print());
        //then
       resultActions.andExpect(status().isOk())
            .andDo(document("admin-sales-calendar",
                   queryParameters(
                           parameterWithName("date").description("조회 달력 월")
                    ),
                    responseFields(
                            fieldWithPath("lastMonthOnMonth").description("전월 대비 %"),
                            fieldWithPath("totalSalesOfMonth").description("이번달 실 매출"),
                            fieldWithPath("totalRefundSalesOfMonth").description("이번달 총 환불액"),
                            fieldWithPath("total[].totalSales").description("일별 총 매출"),
                            fieldWithPath("total[].refundTotalSales").description("일별 총 환불액")
                    )));

}
}