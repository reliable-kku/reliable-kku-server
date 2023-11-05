package com.deundeunhaku.reliablekkuserver.order.controller;

import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesResponse;
import com.deundeunhaku.reliablekkuserver.order.service.AdminOrderService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/sales")
public class AdminOrderSalesController {

  private final AdminOrderService adminOrderService;

  @GetMapping
  public ResponseEntity<AdminSalesResponse> getSales(@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate) {

    if (endDate == null) endDate = startDate;

    return ResponseEntity.ok(adminOrderService.getSalesBetween(startDate, endDate));
  }

}
