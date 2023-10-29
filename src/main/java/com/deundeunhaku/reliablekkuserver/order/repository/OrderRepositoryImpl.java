package com.deundeunhaku.reliablekkuserver.order.repository;

import static com.deundeunhaku.reliablekkuserver.order.domain.QOrder.order;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Order> findOrderListOrderByCreatedAtDescByMember(Member member) {
    return queryFactory.selectFrom(order)
        .where(order.member.eq(member))
        .orderBy(order.createdAt.desc())
        .fetch();
  }
}
