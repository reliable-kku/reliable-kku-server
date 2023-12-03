package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> , OrderRepositoryCustom{

  List<Order> findOrderByCreatedDate(LocalDate createdDate);

  List<Order> findOrderByMember(Member member);

  List<Order> findByOrderStatusInOrderByOrderDatetimeDesc(List<OrderStatus> orderStatus);

  List<Order> findOrderListByMemberAndCreatedDateBetweenAndOrderStatusNotIn(Member member, LocalDate firstDate, LocalDate lastDate, Set<OrderStatus> orderStatus);

  Optional<Order> findFirstByCreatedDateAndOrderStatusNotInOrderByCreatedDateDesc(LocalDate createdAt, List<OrderStatus> orderStatus);

  List<Order> findOrderListByCreatedDateBetween(LocalDate startDate, LocalDate endDate);

  List<Order> findDistinctByMemberOrderByCreatedDate(Member member);
}
