package com.deundeunhaku.reliablekkuserver.order.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminOrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachMenuResponse;
import com.deundeunhaku.reliablekkuserver.order.repository.MenuOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import com.deundeunhaku.reliablekkuserver.sse.dto.SseDataResponse;
import com.deundeunhaku.reliablekkuserver.sse.repository.SSERepository;
import com.deundeunhaku.reliablekkuserver.sse.repository.SseInMemoryRepository;
import com.deundeunhaku.reliablekkuserver.sse.service.SseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
public class AdminOrderService {

  private final OrderRepository orderRepository;
  private final MenuOrderRepository menuOrderRepository;
  private final SseInMemoryRepository sseRepository;
  private final SseService sseService;
  private final ObjectMapper objectMapper;

  public List<AdminOrderResponse> getOrderList(OrderStatus orderStatus) {
    List<Order> orderList = orderRepository.findByOrderStatusOrderByOrderDatetimeAsc(orderStatus);

    return orderList.stream()
        .map(order -> {
          List<OrderEachMenuResponse> eachMenuList = menuOrderRepository.findByOrderToOrderEachMenuResponse(order);
          int totalCount = eachMenuList.stream().mapToInt(OrderEachMenuResponse::count).sum();

          Duration duration = Duration.between(order.getOrderDatetime(), order.getExpectedWaitDatetime());

          return AdminOrderResponse.of(
              order.getTodayOrderCount(),
              order.getMember().getPhoneNumber(),
              order.getOrderDatetime().toLocalTime(),
              order.getIsOfflineOrder(),
              duration.toMinutes(),
              totalCount,
              eachMenuList
          );
        })
        .collect(Collectors.toList());
  }


  @Transactional
  public boolean setOrderToCooking(Long orderId, Integer orderMinutes) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

    order.updateOrderStatus(OrderStatus.COOKING);
    order.addMinutesToExpectedWaitDateTime(orderMinutes);

    return true;
  }

  public void sendMessageToUser(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 주문입니다."));

    Duration leftDuration = Duration.between(order.getExpectedWaitDatetime(),
        order.getOrderDatetime());

    sseService.sendDataToUser(orderId, order.getOrderStatus(), leftDuration.toMinutes());
  }

  @Transactional
  public void deleteOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

//    FIXME : 결제 취소 로직 넣어줘 민진아 ㅠㅠ
    if (order.getMember() != null) {
//      결제 취소 로직
    }

    order.updateOrderStatus(OrderStatus.CANCELED);

  }

  @Transactional
  public void pickUpOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    order.updateOrderStatus(OrderStatus.COOKED);
  }

  public void finishOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    order.updateOrderStatus(OrderStatus.COOKING);
  }

  public void notTakeOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    order.updateOrderStatus(OrderStatus.NOT_TAKE);
  }
}
