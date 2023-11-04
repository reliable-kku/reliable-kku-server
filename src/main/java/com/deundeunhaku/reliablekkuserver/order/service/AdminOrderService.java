package com.deundeunhaku.reliablekkuserver.order.service;

import com.deundeunhaku.reliablekkuserver.fcm.dto.FcmBaseRequest;
import com.deundeunhaku.reliablekkuserver.fcm.service.FcmService;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminOrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachMenuResponse;
import com.deundeunhaku.reliablekkuserver.order.repository.MenuOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentCancelRequest;
import com.deundeunhaku.reliablekkuserver.payment.service.PaymentService;
import com.deundeunhaku.reliablekkuserver.sms.service.SmsService;
import com.deundeunhaku.reliablekkuserver.sse.service.SseService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminOrderService {

  private final OrderRepository orderRepository;
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
      case WAIT ->orderList = orderRepository.findByOrderStatusInOrderByOrderDatetimeAsc(List.of(OrderStatus.WAIT));
      case COOKING -> orderList = orderRepository.findByOrderStatusInOrderByOrderDatetimeAsc(List.of(OrderStatus.COOKING, OrderStatus.COOKED));
      case PICKUP -> orderList = orderRepository.findByOrderStatusInOrderByOrderDatetimeAsc(
          List.of(OrderStatus.PICKUP, OrderStatus.CANCELED, OrderStatus.NOT_TAKE));
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
    }
    order.updateOrderStatus(OrderStatus.CANCELED);
  }

  @Transactional
  public void pickUpOrder(Long orderId) {
    Order order = findByOrderId(orderId);

    order.updateOrderStatus(OrderStatus.COOKED);
  }

  public void finishOrder(Long orderId) {
    Order order = findByOrderId(orderId);

    order.updateOrderStatus(OrderStatus.COOKING);
  }

  public void notTakeOrder(Long orderId) {
    Order order = findByOrderId(orderId);

    order.updateOrderStatus(OrderStatus.COOKING);
  }

}

