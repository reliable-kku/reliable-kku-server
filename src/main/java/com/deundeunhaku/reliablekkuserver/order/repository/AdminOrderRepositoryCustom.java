package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesEachTimeResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface AdminOrderRepositoryCustom {

  List<Order> findOrderByOrderStatusNotInCANCEL(LocalDate startDate, LocalDate endDate);

  List<Order> findOrderByOrderStatusInCANCEL(LocalDate startDate, LocalDate endDate);

  AdminSalesEachTimeResponse findByEachTimeSumOfOrderPriceByDateBetween(LocalDate date, LocalDateTime startTime, LocalDateTime endTime);
}
