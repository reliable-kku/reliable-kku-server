package com.deundeunhaku.reliablekkuserver.order.controller;

import com.deundeunhaku.reliablekkuserver.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order/sse")
public class OrderSseController {

  private final OrderService orderService;

  @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public ResponseEntity<SseEmitter> connect(@RequestParam Long orderId) {

    SseEmitter connectedSseEmitter = orderService.connect(orderId);
    return ResponseEntity.ok(connectedSseEmitter);
  }

}
