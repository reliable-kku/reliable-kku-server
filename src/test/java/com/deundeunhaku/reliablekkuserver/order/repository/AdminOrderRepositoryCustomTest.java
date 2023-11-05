package com.deundeunhaku.reliablekkuserver.order.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.deundeunhaku.reliablekkuserver.BaseRepositoryTest;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminOrderRepositoryCustomTest extends BaseRepositoryTest {

  @Autowired
  private AdminOrderRepository adminOrderRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Test
  void 주문리스트중_취소안된_리스트들을_가져온다() throws Exception {
      //given
      final LocalDate startDate = LocalDate.of(2021, 8, 1);
      final LocalDate endDate = LocalDate.of(2021, 8, 31);

    orderRepository.save(
        Order.builder()
            .todayOrderCount(1L)
            .isOfflineOrder(false)
            .orderStatus(OrderStatus.WAIT)
            .orderPrice(10000)
            .createdDate(LocalDate.of(2021, 8, 1))
            .expectedWaitDatetime(LocalDateTime.of(2021, 8, 1, 12, 0, 0))
            .build()
    );

    orderRepository.save(
        Order.builder()
            .todayOrderCount(1L)
            .isOfflineOrder(false)
            .orderStatus(OrderStatus.WAIT)
            .orderPrice(20000)
            .createdDate(LocalDate.of(2021, 8, 31))
            .expectedWaitDatetime(LocalDateTime.of(2021, 8, 1, 12, 0, 0))
            .build()
    );

    orderRepository.save(
        Order.builder()
            .todayOrderCount(1L)
            .isOfflineOrder(false)
            .orderStatus(OrderStatus.WAIT)
            .orderPrice(20000)
            .createdDate(LocalDate.of(2021, 9, 1))
            .expectedWaitDatetime(LocalDateTime.of(2021, 8, 1, 12, 0, 0))
            .build()
    );

    orderRepository.save(
        Order.builder()
            .todayOrderCount(1L)
            .isOfflineOrder(false)
            .orderStatus(OrderStatus.CANCELED)
            .orderPrice(20000)
            .createdDate(LocalDate.of(2021, 8, 15))
            .expectedWaitDatetime(LocalDateTime.of(2021, 8, 1, 12, 0, 0))
            .build()
    );

      //when
    List<Order> orderList = adminOrderRepository.findOrderByOrderStatusNotInCANCEL(
        startDate, endDate);
    List<Order> cancelOrderList = adminOrderRepository.findOrderByOrderStatusInCANCEL(
        startDate, endDate);

    //then
    assertThat(orderList).hasSize(2);

    assertThat(cancelOrderList).hasSize(1);

  }

}