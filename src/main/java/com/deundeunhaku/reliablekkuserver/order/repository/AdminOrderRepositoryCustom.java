package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import java.time.LocalDate;
import java.util.List;

public interface AdminOrderRepositoryCustom {

  List<Order> findOrderByOrderStatusNotInCANCEL(LocalDate startDate, LocalDate endDate);

  List<Order> findOrderByOrderStatusInCANCEL(LocalDate startDate, LocalDate endDate);
}
