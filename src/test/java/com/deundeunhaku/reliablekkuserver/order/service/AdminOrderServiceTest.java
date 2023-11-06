package com.deundeunhaku.reliablekkuserver.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.deundeunhaku.reliablekkuserver.BaseServiceTest;
import com.deundeunhaku.reliablekkuserver.fcm.service.FcmService;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesEachTimeResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesResponse;
import com.deundeunhaku.reliablekkuserver.order.repository.AdminOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.MenuOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import com.deundeunhaku.reliablekkuserver.payment.service.PaymentService;
import com.deundeunhaku.reliablekkuserver.sms.service.SmsService;
import com.deundeunhaku.reliablekkuserver.sse.service.SseService;
import java.time.LocalDate;
import java.time.LocalTime;
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

}