package com.deundeunhaku.reliablekkuserver.order.service;

import com.deundeunhaku.reliablekkuserver.fcm.dto.FcmBaseRequest;
import com.deundeunhaku.reliablekkuserver.fcm.service.FcmService;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminOrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesEachTimeResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminSalesResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachMenuResponse;
import com.deundeunhaku.reliablekkuserver.order.repository.AdminOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.MenuOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentCancelRequest;
import com.deundeunhaku.reliablekkuserver.payment.service.PaymentService;
import com.deundeunhaku.reliablekkuserver.sms.service.SmsService;
import com.deundeunhaku.reliablekkuserver.sse.repository.SSERepository;
import com.deundeunhaku.reliablekkuserver.sse.service.SseService;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
public class AdminOrderService {

  private final OrderRepository orderRepository;
  private final AdminOrderRepository adminOrderRepository;
  private final MenuOrderRepository menuOrderRepository;
  private final SseService sseService;
  private final FcmService fcmService;
  private final SmsService smsService;
  private final PaymentService paymentService;

  public Order findByOrderId(Long orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 주문입니다."));
  }

  public List<AdminOrderResponse> getOrderList(OrderStatus orderStatus) {

    List<Order> orderList = new ArrayList<>();

    switch (orderStatus) {
      case WAIT -> orderList = orderRepository.findByOrderStatusInOrderByOrderDatetimeAsc(
          List.of(OrderStatus.WAIT));
      case COOKING -> orderList = orderRepository.findByOrderStatusInOrderByOrderDatetimeAsc(
          List.of(OrderStatus.COOKING, OrderStatus.PICKUP));
      case FINISH -> orderList = orderRepository.findByOrderStatusInOrderByOrderDatetimeAsc(
          List.of(OrderStatus.FINISH, OrderStatus.CANCELED, OrderStatus.NOT_TAKE));
    }

    List<AdminOrderResponse> collect = orderList.stream()
        .map(order -> {
          List<OrderEachMenuResponse> eachMenuList = menuOrderRepository.findByOrderToOrderEachMenuResponse(
              order);
          int totalCount = eachMenuList.stream().mapToInt(OrderEachMenuResponse::count).sum();

          Duration duration = Duration.between(order.getOrderDatetime(),
              order.getExpectedWaitDatetime());

          if (order.getIsOfflineOrder()) {
            return AdminOrderResponse.of(
                order.getId(),
                order.getTodayOrderCount(),
                order.getOfflineMember().getPhoneNumber(),
                order.getOrderDatetime().toLocalTime(),
                true,
                duration.toMinutes(),
                totalCount,
                eachMenuList
            );
          }
          return AdminOrderResponse.of(
              order.getId(),
              order.getTodayOrderCount(),
              order.getMember().getPhoneNumber(),
              order.getOrderDatetime().toLocalTime(),
              false,
              duration.toMinutes(),
              totalCount,
              eachMenuList
          );
        })
        .collect(Collectors.toList());
    return collect;
  }


  @Transactional
  public void setOrderToCooking(Long orderId, Integer orderMinutes) {
    Order order = findByOrderId(orderId);

    order.updateOrderStatus(OrderStatus.COOKING);
    order.addMinutesToExpectedWaitDateTime(orderMinutes);
  }

  public void sendOrderSetCookingMessageToUser(Long orderId) {
    Order order = findByOrderId(orderId);

    Duration leftDuration = Duration.between(order.getExpectedWaitDatetime(),
        order.getOrderDatetime());

    if (order.getIsOfflineOrder()) {

      smsService.sendOrderCompleteMessage(order.getOfflineMember().getPhoneNumber(),
          leftDuration.toMinutes());
    } else {

      sseService.sendDataToUser(orderId, order.getOrderStatus(), leftDuration.toMinutes());
      fcmService.sendNotificationByOrderId(FcmBaseRequest.of(
          orderId,
          "접수 완료",
          "안녕하세요! 든붕이 입니다. \n주문이 완료되었습니다. \n" + leftDuration.toMinutes() + "분 후에 완료될 예정입니다."
      ));
    }
  }

  public void sendOrderCancelMessageToUser(Long orderId) {
    Order order = findByOrderId(orderId);

    if (order.getIsOfflineOrder()) {
      smsService.sendOrderCancelMessage(order.getOfflineMember().getPhoneNumber());
    } else {
      fcmService.sendNotificationByOrderId(FcmBaseRequest.of(
          orderId,
          "주문 취소",
          "안녕하세요! 든붕이 입니다. \n 가게의 사정으로 인해 주문이 취소되었습니다. \n다음에 이용해주세요."
      ));
    }
  }

  public void sendOrderPickUpMessageToUser(Long orderId) {
    Order order = findByOrderId(orderId);

    if (order.getIsOfflineOrder()) {
      smsService.sendOrderPickupMessage(order.getOfflineMember().getPhoneNumber());
    } else {
      fcmService.sendNotificationByOrderId(FcmBaseRequest.of(
          orderId,
          "주문 완성",
          "안녕하세요! 든붕이 입니다.\n붕어빵이 완성되었습니다!\n30분 내로 매장에서 붕어빵을 수령해주세요."
      ));
    }
  }

  public void sendOrderFinishMessageToUser(Long orderId) {
    Order order = findByOrderId(orderId);

    if (order.getIsOfflineOrder()) {
      smsService.sendOrderFinishMessage(order.getOfflineMember().getPhoneNumber());
    } else {
      fcmService.sendNotificationByOrderId(FcmBaseRequest.of(
          orderId,
          "주문 완료",
          "안녕하세요! 든붕이 입니다.\n붕어빵 맛있게 드세요! :>"
      ));
    }
  }

  public void sendOrderNotTakeMessageToUser(Long orderId) {
    Order order = findByOrderId(orderId);

    if (order.getIsOfflineOrder()) {
      smsService.sendOrderNotTakeMessage(order.getOfflineMember().getPhoneNumber());
    } else {
      fcmService.sendNotificationByOrderId(FcmBaseRequest.of(
          orderId,
          "주문 미수령",
          "안녕하세요! 든붕이 입니다.\n붕어빵을 시간내에 수령하지 않아 미수령 처리하였습니다."
      ));
    }
  }

  @Transactional
  public void deleteOrder(Long orderId) {
    Order order = findByOrderId(orderId);
    if (order.getMember() != null) {
      paymentService.cancelPayment(orderId, PaymentCancelRequest.of("관리자가 취소"));
      sseService.disconnect(orderId);
    }

    order.updateOrderStatus(OrderStatus.CANCELED);
  }

  @Transactional
  public void pickUpOrder(Long orderId) {
    Order order = findByOrderId(orderId);

    if (order.getOfflineMember() == null) {
      sseService.sendDataToUser(orderId, OrderStatus.PICKUP, 0L);
    }

    order.updateOrderStatus(OrderStatus.PICKUP);
  }

  @Transactional
  public void finishOrder(Long orderId) {
    Order order = findByOrderId(orderId);

    if (order.getOfflineMember() == null) {
      sseService.sendDataToUser(orderId, OrderStatus.FINISH, 0L);
    }

    order.updateOrderStatus(OrderStatus.FINISH);
  }

  @Transactional
  public void notTakeOrder(Long orderId) {
    Order order = findByOrderId(orderId);

    if (order.getOfflineMember() == null) {
      sseService.sendDataToUser(orderId, OrderStatus.NOT_TAKE, 0L);
    }

    order.updateOrderStatus(OrderStatus.NOT_TAKE);
  }

  public AdminSalesResponse getSalesBetween(LocalDate startDate, LocalDate endDate) {

    List<Order> notCancelOrders = adminOrderRepository.findOrderByOrderStatusNotInCANCEL(
        startDate, endDate);

    List<Order> cancelOrders = adminOrderRepository.findOrderByOrderStatusInCANCEL(
        startDate, endDate);

    return AdminSalesResponse.of(
        getSumOfOrderPrices(notCancelOrders),
        getOrdersSize(notCancelOrders),
        getAvgOfOrderPrices(notCancelOrders),
        getSumOfOrderPrices(cancelOrders) * -1,
        getOrdersSize(cancelOrders),
        getAvgOfOrderPrices(cancelOrders) * -1
    );
  }

  private static int getAvgOfOrderPrices(List<Order> orders) {
    return Math.round(
        (float) orders.stream().map(Order::getOrderPrice).reduce(0, Integer::sum)
        / orders.size());
  }

  private static int getOrdersSize(List<Order> orders) {
    return orders.size();
  }

  private static Integer getSumOfOrderPrices(List<Order> orders) {
    return orders.stream().map(Order::getOrderPrice).reduce(0, Integer::sum);
  }

  public List<AdminSalesEachTimeResponse> getEachTimeSalesByDate(LocalDate date) {

    List<AdminSalesEachTimeResponse> responseList = new ArrayList<>();

    for (int hour = 0; hour < 24; hour++) {
      LocalDateTime startTime = LocalDateTime.of(date.getYear(), date.getMonth(),
          date.getDayOfMonth(), hour, 0, 0);
      LocalDateTime endTime = LocalDateTime.of(date.getYear(), date.getMonth(),
          date.getDayOfMonth(), hour, 59, 59);

      responseList.add(adminOrderRepository.findByEachTimeSumOfOrderPriceByDateBetween(date, startTime, endTime));
    }

    return responseList;
  }

  public SseEmitter connectSse() {

    boolean isEmitterExists = sseService.existsEmitterById(0L);

    if (isEmitterExists) {
      SseEmitter sseEmitter = sseService.getEmitter(0L);

      try {
        sseEmitter.send(SseEmitter.event()
            .name("connect")
            .data("성공!"));

      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    }



    return null;
  }
}

