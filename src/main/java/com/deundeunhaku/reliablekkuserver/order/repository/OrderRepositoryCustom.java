package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import java.util.List;

public interface OrderRepositoryCustom {

  List<Order> findOrderListOrderByCreatedAtDescByMember(Member member);
}
