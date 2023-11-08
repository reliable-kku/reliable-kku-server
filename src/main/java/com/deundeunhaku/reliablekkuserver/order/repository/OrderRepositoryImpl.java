package com.deundeunhaku.reliablekkuserver.order.repository;

import static com.deundeunhaku.reliablekkuserver.order.domain.QOrder.order;
import static com.querydsl.core.group.GroupBy.sum;
import static com.querydsl.core.types.ExpressionUtils.count;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.ExcelSalesStatisticsResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.QExcelSalesStatisticsResponse;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Order> findOrderListOrderByCreatedAtDescByMember(Member member) {
    return queryFactory.selectFrom(order)
        .where(order.member.eq(member))
        .orderBy(order.createdAt.desc())
        .fetch();
  }

  @Override
  public ExcelSalesStatisticsResponse findOrderListAllSalesDataByCreateDateBetween(
      LocalDate startDate, LocalDate endDate) {

    return queryFactory.select(
            new QExcelSalesStatisticsResponse(
                order.orderPrice.sum().coalesce(0),
                order.orderPrice.count().coalesce(0L),
                JPAExpressions.select(order.orderPrice.sum().coalesce(0)).from(order)
                    .where(order.orderStatus.eq(OrderStatus.CANCELED)),
                JPAExpressions.select(order.orderPrice.count().coalesce(0L)).from(order)
                    .where(order.orderStatus.eq(OrderStatus.CANCELED)),
                JPAExpressions.select(order.orderPrice.sum().coalesce(0)).from(order)
                    .where(order.isOfflineOrder.eq(false)),
                JPAExpressions.select(order.orderPrice.sum().coalesce(0)).from(order)
                    .where(order.isOfflineOrder.eq(true)
                    )
            ))
        .from(order)
        .where(order.createdDate.between(startDate, endDate))
        .fetchOne();
  }
}
