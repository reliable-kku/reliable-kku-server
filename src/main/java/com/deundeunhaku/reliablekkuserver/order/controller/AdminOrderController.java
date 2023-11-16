package com.deundeunhaku.reliablekkuserver.order.controller;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminOrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachCountResponse;
import com.deundeunhaku.reliablekkuserver.order.service.AdminOrderService;
import java.time.LocalDate;
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

  @GetMapping("/orderCount")
  public OrderEachCountResponse getOrderCount(@RequestParam LocalDate currentDate) {
    return adminOrderService.getOrderCount(currentDate);
  }

  @PostMapping("/{orderId}/minutes/{orderMinutes}")
  public ResponseEntity<Void> changeOrderStatus(
      @PathVariable Long orderId,
      @PathVariable Integer orderMinutes) {

    Order order = adminOrderService.findByOrderId(orderId);
    if (order.getOrderStatus().equals(OrderStatus.CANCELED)) {
      return ResponseEntity.badRequest().build();
    }

    adminOrderService.setOrderToCooking(orderId, orderMinutes);
    adminOrderService.sendOrderSetCookingMessageToUser(orderId);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{orderId}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {

    adminOrderService.deleteOrder(orderId);
    adminOrderService.sendOrderCancelMessageToUser(orderId);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{orderId}/pick-up")
  public ResponseEntity<Void> pickUpOrder(@PathVariable Long orderId) {

    adminOrderService.pickUpOrder(orderId);
    adminOrderService.sendOrderPickUpMessageToUser(orderId);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{orderId}/finish")
  public ResponseEntity<Void> finishOrder(@PathVariable Long orderId) {

    adminOrderService.finishOrder(orderId);
    adminOrderService.sendOrderFinishMessageToUser(orderId);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{orderId}/not-take")
  public ResponseEntity<Void> notTakeOrder(@PathVariable Long orderId) {

    adminOrderService.notTakeOrder(orderId);
    adminOrderService.sendOrderNotTakeMessageToUser(orderId);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{orderId}/recovery")
  public ResponseEntity<Void> recoveryOrder(@PathVariable Long orderId) {

    adminOrderService.setOrderCooking(orderId);

    return ResponseEntity.noContent().build();
  }


}