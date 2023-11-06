package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.ExcelSalesStatisticsResponse;
import com.querydsl.core.Tuple;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepositoryCustom {

  List<Order> findOrderListOrderByCreatedAtDescByMember(Member member);

  ExcelSalesStatisticsResponse findOrderListAllSalesDataByCreateDateBetween(LocalDate startDate, LocalDate endDate);
}
