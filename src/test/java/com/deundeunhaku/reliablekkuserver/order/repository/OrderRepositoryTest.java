package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.BaseRepositoryTest;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

class OrderRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private AdminOrderRepository adminOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 주문시_도장을_찍는다() throws Exception {
        //given
        Member member = Member.builder().id(1L).level(1).build();

        memberRepository.save(member);

        final LocalDate startDate = LocalDate.of(2021, 8, 1);
        final LocalDate endDate = LocalDate.of(2021, 8, 31);
        orderRepository.save(
                Order.builder()
                        .todayOrderCount(1L)
                        .isOfflineOrder(false)
                        .orderStatus(OrderStatus.WAIT)
                        .orderPrice(10000)
                        .member(member)
                        .createdDate(LocalDate.of(2021, 8, 1))
                        .expectedWaitDatetime(LocalDateTime.of(2021, 8, 1, 12, 0, 0))
                        .build()
        );
        //when
        List<Order> orderList = orderRepository.findOrderListByMemberAndCreatedDateBetweenAndOrderStatusNotIn(member, startDate, endDate, Set.of(OrderStatus.CANCELED));

        //then
        Assertions.assertThat(orderList).hasSize(1);
    }
}