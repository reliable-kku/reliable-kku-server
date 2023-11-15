package com.deundeunhaku.reliablekkuserver.order.controller;

import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesCalendarResponse;
import com.deundeunhaku.reliablekkuserver.order.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/sales")
public class AdminOrderSalesCalendarController {

    private final AdminOrderService adminOrderService;

    @GetMapping("/calendar")
    public ResponseEntity<AdminSalesCalendarResponse> getSalesCalendar(@RequestParam(required = false) LocalDate date){
        return ResponseEntity.ok(adminOrderService.getSalesCalendar(date));
    }
}
