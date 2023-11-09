package com.deundeunhaku.reliablekkuserver.order.repository;

import static com.deundeunhaku.reliablekkuserver.order.domain.QOrder.order;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

  @Override
  public AdminSalesEachTimeResponse findByEachTimeSumOfOrderPriceByDateBetween(LocalDate date, LocalDateTime startTime,
      LocalDateTime endTime) {
    return queryFactory.select(
            new QAdminSalesEachTimeResponse(
                Expressions.asDateTime(startTime),
                order.orderPrice.sum().coalesce(0)
            )
        )
        .from(order)
        .where(order.createdDate.eq(date))
        .where(order.orderDatetime.between(startTime, endTime))
        .fetchOne();
  }

  @Override
  public Integer findCalendarMonthDataByStartDateAndLastDateBetween(LocalDate startDate, LocalDate lastDate) {
    return queryFactory.select(
                    order.orderPrice.sum().coalesce(0).as("sumOfPriceOfMonth")
            )
            .from(order)
            .where(order.orderStatus.ne(OrderStatus.CANCELED))
            .where(order.createdDate.between(startDate, lastDate))
            .fetchOne();
  }

  @Override
  public Integer findTotalRefundSalesOfMonthByStartDateAndLastDateBetween(LocalDate startDate, LocalDate lastDate) {
    return queryFactory.select(
                            order.orderPrice.sum().coalesce(0).as("sumOfRefundPriceOfMonth")
                    )
            .from(order)
            .where(order.orderStatus.eq(OrderStatus.CANCELED))
            .where(order.createdDate.between(startDate, lastDate))
            .fetchOne();

  }

  @Override
  public TotalSalesMonthOfDay findTotalSalesMonthOfDayByDate(LocalDate date) {
    Integer sumOfPriceOfDay = queryFactory.select(order.orderPrice.sum().coalesce(0))
            .from(order)
            .where(order.createdDate.eq(date))
            .where(order.orderStatus.ne(OrderStatus.CANCELED))
            .fetchFirst();

    Integer sumOfRefundPriceOfDay = queryFactory.select(order.orderPrice.sum().coalesce(0))
            .from(order)
            .where(order.createdDate.eq(date))
            .where(order.orderStatus.eq(OrderStatus.CANCELED))
            .fetchFirst();

    if (sumOfPriceOfDay == null) {
      sumOfPriceOfDay = 0;
    }

    if (sumOfRefundPriceOfDay == null) {
      sumOfRefundPriceOfDay = 0;
    }

    return new TotalSalesMonthOfDay(sumOfPriceOfDay, sumOfRefundPriceOfDay);
  }
}
