package com.deundeunhaku.reliablekkuserver.order.service;

import com.deundeunhaku.reliablekkuserver.BaseServiceTest;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderCalendarResponse;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class OrderServiceTest extends BaseServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void 회원의_마이페이지_캘린더_리스트를_가져오는지_검증한다(){
        //given
        Order order1 = Order.builder().createdDate(LocalDate.of(2023, 10, 24)).orderStatus(OrderStatus.FINISH).build();
        Order order2 = Order.builder().createdDate(LocalDate.of(2023, 10, 23)).orderStatus(OrderStatus.FINISH).build();
        Order order3 = Order.builder().createdDate(LocalDate.of(2023, 10, 21)).orderStatus(OrderStatus.FINISH).build();
        List<Order> orderList = List.of(order1, order2, order3);
        Integer year = 2023;
        Integer month = 10;
        LocalDate firstDate = LocalDate.of(year, month, 1);
        LocalDate lastDate = firstDate.plusMonths(1L).minusDays(1L);
        when(orderRepository.findOrderListByMemberAndCreatedDateBetweenAndOrderStatusNotIn(any(), eq(firstDate), eq(lastDate), eq(Set.of(OrderStatus.CANCELED)))).thenReturn(orderList);

        //when
        List<OrderCalendarResponse> calendarList = orderService.getOrderListByMemberAndYearAndMonth(Member.builder().build(), year, month);

        //then
        Integer realMonth = 10;
        MonthDay monthDay = MonthDay.of(realMonth, 1);
        int daysInMonth = monthDay.getMonth().maxLength();
        assertThat(calendarList).hasSize(daysInMonth);
        assertThat(calendarList.get(0).getOrderedDay()).isEqualTo(1);
        assertThat(calendarList.get(0).getIsOrdered()).isFalse();
        assertThat(calendarList.get(20).getOrderedDay()).isEqualTo(21);
        assertThat(calendarList.get(20).getIsOrdered()).isTrue();
        assertThat(calendarList.get(22).getOrderedDay()).isEqualTo(23);
        assertThat(calendarList.get(22).getIsOrdered()).isTrue();

    }

}