package com.deundeunhaku.reliablekkuserver.order.controller;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminOrderResponse;
import com.deundeunhaku.reliablekkuserver.order.service.AdminOrderService;
import com.deundeunhaku.reliablekkuserver.sse.service.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    Order order = adminOrderService.findByOrderId(orderId);

    adminOrderService.setOrderToCooking(order, orderMinutes);
    adminOrderService.sendOrderSetCookingMessageToUser(order);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{orderId}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
    Order order = adminOrderService.findByOrderId(orderId);

    adminOrderService.deleteOrder(order);
    adminOrderService.sendOrderCancelMessageToUser(order);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{orderId}/pick-up")
  public ResponseEntity<Void> pickUpOrder(@PathVariable Long orderId) {
    Order order = adminOrderService.findByOrderId(orderId);

    adminOrderService.pickUpOrder(order);
    adminOrderService.sendOrderPickUpMessageToUser(order);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{orderId}/finish")
  public ResponseEntity<Void> finishOrder(@PathVariable Long orderId) {
    Order order = adminOrderService.findByOrderId(orderId);

    adminOrderService.finishOrder(order);
    adminOrderService.sendOrderFinishMessageToUser(order);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{orderId}/not-take")
  public ResponseEntity<Void> notTakeOrder(@PathVariable Long orderId) {
    Order order = adminOrderService.findByOrderId(orderId);

    adminOrderService.notTakeOrder(order);
    adminOrderService.sendOrderNotTakeMessageToUser(order);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{orderId}/recovery")
  public ResponseEntity<Void> recoveryOrder(@PathVariable Long orderId) {
    Order order = adminOrderService.findByOrderId(orderId);

    adminOrderService.setOrderCooking(order);
    return ResponseEntity.noContent().build();
  }


}