package com.deundeunhaku.reliablekkuserver.order.controller;


import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderCalendarResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderIdResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderRegisterRequest;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.PastOrderResponse;
import com.deundeunhaku.reliablekkuserver.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<OrderIdResponse> registerOrder(@RequestBody OrderRegisterRequest request, @AuthenticationPrincipal
      Member member){
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderService.registerOrder(request, member));
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<OrderResponse> getOrderId(@PathVariable Long orderId) {
    return ResponseEntity.ok(orderService.getOrderMenuList(orderId));
  }

  @DeleteMapping("/{orderId}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
    orderService.deleteOrder(orderId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/duplicate")
  public ResponseEntity<Void> isMemberNowOrdered(@AuthenticationPrincipal Member member) {
    orderService.isMemberNowOrdered(member);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/calendar")
  public ResponseEntity<List<OrderCalendarResponse>> getCalendarForMemberAndOrderDate(@AuthenticationPrincipal Member member,
                                                                          @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
    if (year == null || month == null) {
      year = LocalDate.now().getYear();
      month = LocalDate.now().getMonthValue();
    }

    List<OrderCalendarResponse> calenderList = orderService.getOrderListByMemberAndYearAndMonth(member, year, month);
    return ResponseEntity.ok(calenderList);
  }

  @GetMapping("/past")
  public ResponseEntity<List<PastOrderResponse>> getPastOrderList(@AuthenticationPrincipal Member member) {
    return ResponseEntity.ok(orderService.getPastOrderList(member));
  }
}
