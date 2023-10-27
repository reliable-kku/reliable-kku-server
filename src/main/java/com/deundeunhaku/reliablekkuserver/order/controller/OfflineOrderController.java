package com.deundeunhaku.reliablekkuserver.order.controller;

import com.deundeunhaku.reliablekkuserver.order.dto.OfflineOrderRequest;
import com.deundeunhaku.reliablekkuserver.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/offline-orders")
public class OfflineOrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<Void> createOfflineOrder(@RequestBody OfflineOrderRequest request) {
    orderService.createOfflineOrder(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

}
