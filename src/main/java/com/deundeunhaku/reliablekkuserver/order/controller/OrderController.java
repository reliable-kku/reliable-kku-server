package com.deundeunhaku.reliablekkuserver.order.controller;


import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderIdResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderRegisterRequest;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderResponse;
import com.deundeunhaku.reliablekkuserver.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
