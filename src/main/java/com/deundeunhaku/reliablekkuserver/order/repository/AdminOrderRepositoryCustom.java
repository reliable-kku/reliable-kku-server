package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesCalendarResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesEachTimeResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.TotalSalesMonthOfDay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminOrderRepositoryCustom {

  List<Order> findOrderByOrderStatusNotInCANCEL(LocalDate startDate, LocalDate endDate);

  List<Order> findOrderByOrderStatusInCANCEL(LocalDate startDate, LocalDate endDate);

  AdminSalesEachTimeResponse findByEachTimeSumOfOrderPriceByDateBetween(LocalDate date, LocalDateTime startTime, LocalDateTime endTime);

  Integer findCalendarMonthDataByStartDateAndLastDateBetween(LocalDate startDate, LocalDate lastDate);

  Integer findTotalRefundSalesOfMonthByStartDateAndLastDateBetween(LocalDate startDate, LocalDate lastDate);

  TotalSalesMonthOfDay findTotalSalesMonthOfDayByDate(LocalDate date);
}
