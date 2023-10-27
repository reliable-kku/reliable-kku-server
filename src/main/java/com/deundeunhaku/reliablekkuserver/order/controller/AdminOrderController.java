package com.deundeunhaku.reliablekkuserver.order.controller;

import com.amazonaws.Response;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminOrderResponse;
import com.deundeunhaku.reliablekkuserver.order.service.AdminOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {

  private final AdminOrderService adminOrderService;

  @GetMapping
  public List<AdminOrderResponse> getOrderList(@RequestParam OrderStatus orderStatus) {
    return adminOrderService.getOrderList(orderStatus);
  }

  @PostMapping("/{orderId}/minutes/{orderMinutes}")
  public ResponseEntity<Void> changeOrderStatus(
      @PathVariable Long orderId,
      @PathVariable Integer orderMinutes) {
    adminOrderService.setOrderToCooking(orderId, orderMinutes);
    return ResponseEntity.status(HttpStatus.CREATED).build();

  }

}
