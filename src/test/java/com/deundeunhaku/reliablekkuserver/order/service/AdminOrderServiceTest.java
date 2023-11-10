package com.deundeunhaku.reliablekkuserver.order.service;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.deundeunhaku.reliablekkuserver.BaseServiceTest;
import com.deundeunhaku.reliablekkuserver.fcm.service.FcmService;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.*;
import com.deundeunhaku.reliablekkuserver.order.repository.AdminOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.MenuOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import com.deundeunhaku.reliablekkuserver.payment.service.PaymentService;
import com.deundeunhaku.reliablekkuserver.sms.service.SmsService;
import com.deundeunhaku.reliablekkuserver.sse.service.SseService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;

class AdminOrderServiceTest extends BaseServiceTest {

  @InjectMocks
  private AdminOrderService adminOrderService;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private AdminOrderRepository adminOrderRepository;

  @Mock
  private MenuOrderRepository menuOrderRepository;

  @Mock
  private SseService sseService;

  @Mock
  private FcmService fcmService;

  @Mock
  private SmsService smsService;

  @Mock
  private PaymentService paymentService;

  @Test
  void 주문_상태를_반환한다() throws Exception {
      //given
    LocalDate startDate = LocalDate.of(2021, 1, 1);
    LocalDate endDate = LocalDate.of(2021, 1, 31);

    when(adminOrderRepository.findOrderByOrderStatusNotInCANCEL(startDate, endDate)).thenReturn(
        List.of(
            Order.builder().orderPrice(100).build(),
            Order.builder().orderPrice(200).build(),
            Order.builder().orderPrice(300).build()
        )
    );

    when(adminOrderRepository.findOrderByOrderStatusInCANCEL(startDate, endDate)).thenReturn(
        List.of(
            Order.builder().orderPrice(400).build(),
            Order.builder().orderPrice(500).build(),
            Order.builder().orderPrice(600).build()
        ));
      //when

    AdminSalesResponse response = adminOrderService.getSalesBetween(startDate, endDate);

    //then

    assertThat(response.getRealAmount()).isEqualTo(600);
    assertThat(response.getPaymentCount()).isEqualTo(3);
    assertThat(response.getAvgPaymentAmount()).isEqualTo(200);
    assertThat(response.getRefundAmount()).isEqualTo(-1500);
    assertThat(response.getRefundCount()).isEqualTo(3);
    assertThat(response.getAvgRefundAmount()).isEqualTo(-500);
  }

  @Test
  void 일별_한시간별_주문의합을_반환한다() throws Exception {
      //given
    LocalDate date = LocalDate.of(2021, 1, 1);

    when(adminOrderRepository.findByEachTimeSumOfOrderPriceByDateBetween(any(), any(), any())).thenReturn(
        AdminSalesEachTimeResponse.of(
            LocalTime.of(12, 30),
            100
        )
    );
      //when
    List<AdminSalesEachTimeResponse> responseList = adminOrderService.getEachTimeSalesByDate(
        date);

    //then
    assertThat(responseList).hasSize(24);

  }

  @Test
  void 날짜메소드테스트() throws Exception {
      //given
    LocalDate today = LocalDate.of(2023, 11, 7);
    System.out.println("today = " + today);

//    LocalDate expect = LocalDate.from(today.plusMonths(1)..minusDays(1));
    LocalDate expect = today.with(TemporalAdjusters.firstDayOfMonth());
    LocalDate expect2 = today.with(TemporalAdjusters.lastDayOfMonth());
    System.out.println("expect = " + expect);
    System.out.println("expect2 = " + expect2);


    //when

      //then
  }
  @Test
  void 매출관리_달력_데이터를_반환한다() throws Exception {
      //given
      LocalDate date = LocalDate.of(2023, 11, 2);

      Order order1 = Order.builder().id(1L).orderPrice(3500).createdDate(LocalDate.of(2023, 11, 2)).orderStatus(OrderStatus.FINISH).build();
      Order order2 = Order.builder().id(2L).orderPrice(4500).createdDate(LocalDate.of(2023, 11, 3)).orderStatus(OrderStatus.FINISH).build();
      Order order3 = Order.builder().id(3L).orderPrice(4500).createdDate(LocalDate.of(2023, 11, 4)).orderStatus(OrderStatus.CANCELED).build();
      Order order8 = Order.builder().id(8L).orderPrice(4500).createdDate(LocalDate.of(2023, 11, 4)).orderStatus(OrderStatus.FINISH).build();
      Order order4 = Order.builder().id(4L).orderPrice(5500).createdDate(LocalDate.of(2023, 10, 2)).orderStatus(OrderStatus.FINISH).build();
      Order order5 = Order.builder().id(5L).orderPrice(6500).createdDate(LocalDate.of(2023, 10, 3)).orderStatus(OrderStatus.FINISH).build();
      Order order6 = Order.builder().id(6L).orderPrice(2500).createdDate(LocalDate.of(2023, 10, 4)).orderStatus(OrderStatus.FINISH).build();
      Order order7 = Order.builder().id(7L).orderPrice(2500).createdDate(LocalDate.of(2023, 10, 4)).orderStatus(OrderStatus.CANCELED).build();

      List orderList = List.of(order1,order2,order3,order4,order5,order6,order7,order8);
      LocalDate lastMonth = date.minusMonths(1);

      LocalDate lastMonthFirstDay = lastMonth.with(firstDayOfMonth());
      LocalDate lastMonthLastDay = lastMonth.with(lastDayOfMonth());

      when(adminOrderRepository.findCalendarMonthDataByStartDateAndLastDateBetween(lastMonthFirstDay, lastMonthLastDay)).thenReturn(10000);

      LocalDate thisMonthFirstDay = date.with(firstDayOfMonth());
      LocalDate thisMonthLastDay = date.with(lastDayOfMonth());

      when(adminOrderRepository.findCalendarMonthDataByStartDateAndLastDateBetween(thisMonthFirstDay, thisMonthLastDay)).thenReturn(100000);
      when(adminOrderRepository.findTotalRefundSalesOfMonthByStartDateAndLastDateBetween(thisMonthFirstDay, thisMonthLastDay)).thenReturn(3000);

      List<TotalSalesMonthOfDay> monthOfDaysList = new ArrayList<>();
      for (int day = 1; day <= date.with(lastDayOfMonth()).getDayOfMonth(); day++) {
          LocalDate eachDay = LocalDate.of(date.getYear(), date.getMonth(), day);
          TotalSalesMonthOfDay dayData = new TotalSalesMonthOfDay(7000, 700);
          monthOfDaysList.add(dayData);
          when(adminOrderRepository.findTotalSalesMonthOfDayByDate(eachDay)).thenReturn(dayData);
      }
    //when
    AdminSalesCalendarResponse response = adminOrderService.getSalesCalendar(date);
      //then
    assertThat(response.lastMonthOnMonth()).isEqualTo(900);
    assertThat(response.totalSalesOfMonth()).isEqualTo(100000);
    assertThat(response.totalRefundSalesOfMonth()).isEqualTo(3000);
    assertThat(response.total()).isEqualTo(monthOfDaysList);
  }
  
  @Test
  void 대기_접수_완료의_주문_개수를_반환한다(){
      //given
      Order.builder().createdDate(LocalDate.of(2023, 11, 11)).orderStatus(OrderStatus.WAIT).build();
      Order.builder().createdDate(LocalDate.of(2023, 11, 11)).orderStatus(OrderStatus.COOKING).build();
      Order.builder().createdDate(LocalDate.of(2023, 11, 11)).orderStatus(OrderStatus.PICKUP).build();
      Order.builder().createdDate(LocalDate.of(2023, 11, 11)).orderStatus(OrderStatus.CANCELED).build();
      Order.builder().createdDate(LocalDate.of(2023, 11, 11)).orderStatus(OrderStatus.FINISH).build();
      Order.builder().createdDate(LocalDate.of(2023, 11, 11)).orderStatus(OrderStatus.NOT_TAKE).build();
      LocalDate currentDate = LocalDate.of(2023, 11, 11);
      when(orderRepository.findOrderCountByOrderStatusAndDate(List.of(OrderStatus.WAIT), currentDate)).thenReturn(1L);
      when(orderRepository.findOrderCountByOrderStatusAndDate(List.of(OrderStatus.COOKING, OrderStatus.PICKUP), currentDate)).thenReturn(2L);
      when(orderRepository.findOrderCountByOrderStatusAndDate(List.of(OrderStatus.FINISH, OrderStatus.CANCELED, OrderStatus.NOT_TAKE), currentDate)).thenReturn(3L);
      //when
      OrderEachCountResponse response = adminOrderService.getOrderCount(currentDate);
      //then
      assertThat(response.waitOrderCount()).isEqualTo(1);
      assertThat(response.cookingOrderCount()).isEqualTo(2);
      assertThat(response.finishOrderCount()).isEqualTo(3);
  }
}