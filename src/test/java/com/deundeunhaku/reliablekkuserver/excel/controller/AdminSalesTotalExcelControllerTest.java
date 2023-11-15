package com.deundeunhaku.reliablekkuserver.excel.controller;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.ExcelSalesStatisticsResponse;
import com.deundeunhaku.reliablekkuserver.order.repository.MenuOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminSalesTotalExcelControllerTest extends BaseControllerTest {

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    MenuOrderRepository menuOrderRepository;

    @Test
    void 매출관리에서_선택한_날짜에_따라_엑셀을_다운로드한다() throws Exception {
        //given
        LocalDate startDate = LocalDate.of(2023, 11, 1);
        LocalDate endDate = LocalDate.of(2023, 11, 6);
        List<Order> orderList = orderRepository.findOrderListByCreatedDateBetween(startDate, endDate);
        ExcelSalesStatisticsResponse total = orderRepository.findOrderListAllSalesDataByCreateDateBetween(startDate, endDate);


        when(orderRepository.findOrderListByCreatedDateBetween(startDate, endDate))
                .thenReturn(
                        List.of(
                                Order.builder()
                                        .createdDate(LocalDate.of(2023, 11, 1))
                                        .createdAt(LocalDateTime.of(2023, 11, 1, 16, 30, 12))
                                        .orderPrice(3000)
                                        .isOfflineOrder(false)
                                                .build()
                        )
                );

        when(orderRepository.findOrderListAllSalesDataByCreateDateBetween(startDate, endDate)).thenReturn(
            ExcelSalesStatisticsResponse.of(
                    150000,
                    3000L,
                    10000,
                    100L,
                    130000,
                    20000
            )
        );

        List<String> testMenuNames = Arrays.asList("Menu1", "Menu2", "Menu3"); // 테스트용 메뉴 이름 목록
        when(menuOrderRepository.findMenuNameByEachMenuOrder(Order.builder().build())).thenReturn(testMenuNames);

        SXSSFWorkbook workbook = new SXSSFWorkbook();

        File tmpFile = File.createTempFile("TMP~", ".xlsx");
        try (OutputStream fos = new FileOutputStream(tmpFile);) {
            workbook.write(fos);
        }
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/admin/excel")
                .param("startDate", "2023-11-01")
                .param("endDate", "2023-11-06")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("admin/excel/success",
                        queryParameters(
                                parameterWithName("startDate").description("달력에서 처음 선택한 날짜"),
                                parameterWithName("endDate").description("달력에서 마지막으로 선택한 날짜")
                        )
                ));
    }

}