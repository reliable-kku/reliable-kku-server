package com.deundeunhaku.reliablekkuserver.order.repository;

import static com.deundeunhaku.reliablekkuserver.menu.domain.QMenu.menu;
import static com.deundeunhaku.reliablekkuserver.order.domain.QMenuOrder.menuOrder;

import com.deundeunhaku.reliablekkuserver.menu.domain.QMenu;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.domain.QMenuOrder;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachMenuResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.QOrderEachMenuResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MenuOrderRepositoryImpl implements MenuOrderRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<OrderEachMenuResponse> findByOrderToOrderEachMenuResponse(Order order) {
    return queryFactory
        .select(
            new QOrderEachMenuResponse(menu.name, menuOrder.count)
        ).from(menu)
        .innerJoin(menuOrder.menu, menu)
        .fetch();
  }
}
