package com.deundeunhaku.reliablekkuserver.order.service;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.domain.OfflineMember;
import com.deundeunhaku.reliablekkuserver.member.repository.OfflineMemberRepository;
import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import com.deundeunhaku.reliablekkuserver.menu.repository.MenuRepository;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.MenuOrder;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.MenuResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OfflineOrderRequest;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderIdResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderRegisterRequest;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.RegisteredMenuRequest;
import com.deundeunhaku.reliablekkuserver.order.repository.MenuOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import com.deundeunhaku.reliablekkuserver.sse.repository.SseInMemoryRepository;
import com.deundeunhaku.reliablekkuserver.toss.Payment;
import com.deundeunhaku.reliablekkuserver.toss.PaymentRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final PaymentRepository paymentRepository;
  private final MenuOrderRepository menuOrderRepository;
  private final MenuRepository menuRepository;
  private final OfflineMemberRepository offlineMemberRepository;
  private final SseInMemoryRepository sseRepository;

  @Transactional
  public OrderIdResponse registerOrder(OrderRegisterRequest request, Member member) {
    Payment payment = paymentRepository.findById(request.tossOrderId())
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    if (!payment.getIsPaid()) {
      throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    List<Order> orderByMember = orderRepository.findOrderByMember(member);
    List<OrderStatus> orderProceedingStatus = List.of(OrderStatus.WAIT, OrderStatus.COOKING,
        OrderStatus.COOKED);

    List<OrderStatus> isOrderProceeding = orderByMember.stream()
        .map(Order::getOrderStatus)
        .filter(orderProceedingStatus::contains)
        .toList();

    if (isOrderProceeding.size() > 0) {
      throw new IllegalArgumentException("이미 주문이 진행중입니다.");
    }

    Long maxId = getMaxTodayOrderCount();

    Order onlineOrder = Order.createOnlineOrder(maxId + 1, request, member);
    Order savedOrder = orderRepository.save(onlineOrder);

    List<RegisteredMenuRequest> menuRequestList = request.registeredMenus();

    for (RegisteredMenuRequest menuRequest : menuRequestList) {
      Menu menu = menuRepository.findById(menuRequest.menuId())
          .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
      menuOrderRepository.save(MenuOrder.createMenuOrder(menu, savedOrder, menuRequest.count()));
    }

    return OrderIdResponse.of(savedOrder.getId());
  }

  public void createOfflineOrder(OfflineOrderRequest request) {

    OfflineMember offlineMember = saveOfflineMember(
        request);

    Long maxId = getMaxTodayOrderCount();

    Order offlineOrder = Order.createOfflineOrder(maxId, request, offlineMember);
    orderRepository.save(offlineOrder);

    List<RegisteredMenuRequest> menuRequestList = request.registeredMenus();

    for (RegisteredMenuRequest menuRequest : menuRequestList) {
      Menu menu = menuRepository.findById(menuRequest.menuId())
          .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
      menuOrderRepository.save(MenuOrder.createMenuOrder(menu, offlineOrder, menuRequest.count()));
    }

  }

  public SseEmitter connect(Long orderId) {

    Optional<SseEmitter> optionalSseEmitter = sseRepository.get(orderId);

    if (optionalSseEmitter.isPresent()) {
      return optionalSseEmitter.get();
    } else {
      SseEmitter sseEmitter = new SseEmitter();

      sseRepository.put(orderId, sseEmitter);
      sseEmitter.onCompletion(() -> sseRepository.remove(orderId));
      sseEmitter.onTimeout(() -> sseRepository.remove(orderId));

      try {
        int object = -100;
        sseEmitter.send(SseEmitter.event()
            .name("status")
            .data(object));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      return sseEmitter;
    }
  }


  public OrderResponse getOrderMenuList(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    List<MenuOrder> menuOrderList = menuOrderRepository.findByOrder(order);

    List<MenuResponse> menuResponseList = menuOrderList.stream()
        .map(menuOrder -> {
          Menu menu = menuOrder.getMenu();
          return MenuResponse.of(menu.getName(), menuOrder.getCount());
        })
        .collect(Collectors.toList());

    return OrderResponse.of(order.getOrderPrice(), menuResponseList);
  }

  @Transactional
  public void deleteOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    order.updateOrderStatus(OrderStatus.CANCELED);
  }


  private OfflineMember saveOfflineMember(OfflineOrderRequest request) {
    return offlineMemberRepository.save(
        OfflineMember.builder()
            .phoneNumber(request.phoneNumber())
            .build()
    );
  }

  private Long getMaxTodayOrderCount() {
    List<Order> todayOrderList = orderRepository.findOrderByCreatedAt(LocalDate.now());

    return todayOrderList.stream()
        .map(Order::getTodayOrderCount)
        .max(Long::compare)
        .orElse(0L);
  }

}