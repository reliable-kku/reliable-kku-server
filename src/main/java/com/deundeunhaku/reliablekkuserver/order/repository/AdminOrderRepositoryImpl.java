package com.deundeunhaku.reliablekkuserver.order.repository;

import static com.deundeunhaku.reliablekkuserver.order.domain.QOrder.order;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminOrderRepositoryImpl implements AdminOrderRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Order> findOrderByOrderStatusNotInCANCEL(LocalDate startDate, LocalDate endDate) {
    return queryFactory.selectFrom(order)
        .where(order.createdDate.between(startDate, endDate))
        .where(order.orderStatus.notIn(List.of(OrderStatus.CANCELED)))
        .fetch();
  }

  @Override
  public List<Order> findOrderByOrderStatusInCANCEL(LocalDate startDate, LocalDate endDate) {
    return queryFactory.selectFrom(order)
        .where(order.createdDate.between(startDate, endDate))
        .where(order.orderStatus.in(List.of(OrderStatus.CANCELED)))
        .fetch();
  }
}
