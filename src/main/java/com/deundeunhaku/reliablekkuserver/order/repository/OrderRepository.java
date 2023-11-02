package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> , OrderRepositoryCustom{

  List<Order> findOrderByCreatedAt(LocalDate createdAt);

  List<Order> findOrderByMember(Member member);

  List<Order> findByOrderStatusOrderByOrderDatetimeAsc(OrderStatus orderStatus);

  List<Order> findOrderListByMemberAndCreatedDateBetween(Member member, LocalDate firstDate, LocalDate lastDate);

  Optional<Order> findFirstByCreatedAtOrderByCreatedAtDesc(LocalDate createdAt);
}
