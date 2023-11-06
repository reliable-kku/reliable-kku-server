package com.deundeunhaku.reliablekkuserver.order.service;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.domain.OfflineMember;
import com.deundeunhaku.reliablekkuserver.member.repository.OfflineMemberRepository;
import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import com.deundeunhaku.reliablekkuserver.menu.repository.MenuRepository;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.MenuOrder;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.LeftTimeResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OfflineOrderRequest;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderCalendarResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachMenuResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderIdResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderRegisterRequest;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.PastOrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.RegisteredMenuRequest;
import com.deundeunhaku.reliablekkuserver.order.repository.MenuOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import com.deundeunhaku.reliablekkuserver.payment.domain.Payment;
import com.deundeunhaku.reliablekkuserver.payment.repository.PaymentRepository;
import com.deundeunhaku.reliablekkuserver.sse.dto.SseDataResponse;
import com.deundeunhaku.reliablekkuserver.sse.repository.SseInMemoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final PaymentRepository paymentRepository;
  private final MenuOrderRepository menuOrderRepository;
  private final MenuRepository menuRepository;
  private final OfflineMemberRepository offlineMemberRepository;
  private final SseInMemoryRepository sseRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public OrderIdResponse registerOrder(OrderRegisterRequest request, Member member) {
    Payment payment = paymentRepository.findByTossOrderId(request.tossOrderId())
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    if (!payment.isPaySuccessYn()) {
      throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    List<Order> orderByMember = orderRepository.findOrderByMember(member);
    List<OrderStatus> orderProceedingStatus = List.of(OrderStatus.WAIT, OrderStatus.COOKING,
        OrderStatus.PICKUP);

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

    Order offlineOrder = Order.createOfflineOrder(maxId + 1, request, offlineMember);
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

    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 주문입니다."));

    Duration leftDuration = Duration.between(order.getExpectedWaitDatetime(),
        order.getOrderDatetime());
    SseDataResponse response = SseDataResponse.of(order.getOrderStatus(),
        leftDuration.toMinutes());

    try {

      if (optionalSseEmitter.isPresent()) {
        SseEmitter sseEmitter = optionalSseEmitter.get();

        sseEmitter.send(SseEmitter.event()
            .name("status")
            .data(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON)
        );
        return sseEmitter;
      } else {
        SseEmitter sseEmitter = new SseEmitter();
        log.info("SseEmitter 생성 {}", sseEmitter.toString());

        sseRepository.put(orderId, sseEmitter);

        sseEmitter.onCompletion(() -> sseRepository.remove(orderId));
        sseEmitter.onTimeout(() -> sseRepository.remove(orderId));

        sseEmitter.send(SseEmitter.event()
            .name("status")
            .data(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
        return sseEmitter;
      }
    } catch (IOException e) {
      throw new IllegalArgumentException("잘못된 요청입니다.");
    }
  }


  public OrderResponse getOrderMenuList(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    List<MenuOrder> menuOrderList = menuOrderRepository.findByOrder(order);

    List<OrderEachMenuResponse> menuResponseList = menuOrderList.stream()
        .map(menuOrder -> {
          Menu menu = menuOrder.getMenu();
          return OrderEachMenuResponse.of(menu.getName(), menuOrder.getCount());
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
    List<Order> todayOrderList = orderRepository.findOrderByCreatedDate(LocalDate.now());

    return todayOrderList.stream()
        .map(Order::getTodayOrderCount)
        .max(Long::compare)
        .orElse(0L);
  }

  public OrderIdResponse isMemberNowOrdered(Member member) {
    List<Order> orderByMember = orderRepository.findOrderByMember(member);
    List<OrderStatus> orderProceedingStatus = List.of(OrderStatus.WAIT, OrderStatus.COOKING, OrderStatus.PICKUP);

    List<Order> orderingOrders = orderByMember.stream()
        .filter(o -> orderProceedingStatus.contains(o.getOrderStatus()))
        .toList();

    if (orderingOrders.size() > 0) {
      return OrderIdResponse.of(orderingOrders.get(0).getId());
    }
    throw new IllegalArgumentException("주문이 없습니다.");

  }

  public List<OrderCalendarResponse> getOrderListByMemberAndYearAndMonth(Member member,
      Integer year, Integer month) {

    LocalDate firstDate = LocalDate.of(year, month, 1);
    LocalDate lastDate = firstDate.plusMonths(1L).minusDays(1L);

    List<Order> orders = orderRepository.findOrderListByMemberAndCreatedDateBetween(member,
        firstDate,
        lastDate);

    List<OrderCalendarResponse> responseList = new ArrayList<>(orders.stream()
        .map(order -> {
              Integer orderDay = order.getCreatedDate().getDayOfMonth();
              return OrderCalendarResponse.of(orderDay, true);
            }
        ).toList());

    addNotOrderedDayList(year, month, responseList);
    return responseList.stream().sorted(Comparator.comparing(OrderCalendarResponse::getOrderedDay))
        .collect(Collectors.toList());
  }

  private static void addNotOrderedDayList(Integer year, Integer month,
      List<OrderCalendarResponse> responseList) {
    for (int day = 1; day <= Month.of(month).length(year % 4 == 0); day++) {
      Integer finalDay = day;
      if (responseList.stream().noneMatch(response -> response.getOrderedDay().equals(finalDay))) {
        responseList.add(OrderCalendarResponse.of(day, false));
      }
    }
  }


  public List<PastOrderResponse> getPastOrderList(Member member) {
    List<Order> orderList = orderRepository.findOrderListOrderByCreatedAtDescByMember(member);

    return orderList.stream()
        .map(order -> {
          List<OrderEachMenuResponse> eachMenuList = menuOrderRepository.findByOrderToOrderEachMenuResponse(
              order);
          log.info("orderId : {}, :{} ", order.getId(), eachMenuList);
          return PastOrderResponse.of(order.getCreatedAt().toLocalDate(),
              order.getOrderDatetime().toLocalTime(),
              order.getOrderPrice(),
              eachMenuList);
        })
        .toList();
  }

  @Transactional
  public void deleteOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    orderRepository.delete(order);
  }

  public Long findMemberIdWithOrderId(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

    return order.getMember().getId();
  }

  public LeftTimeResponse getLeftTime() {
    LocalDateTime nowDateTime = LocalDateTime.now();

    Order todayLastOrder = orderRepository.findFirstByCreatedDateOrderByCreatedDateDesc(
            nowDateTime.toLocalDate())
        .orElse(Order.builder().expectedWaitDatetime(nowDateTime).build());

    Duration between = Duration.between(nowDateTime, todayLastOrder.getExpectedWaitDatetime());

//   현재 날짜가 주문 마감 시간보다 늦거나 같으면 0분으로 반환
    if (nowDateTime.isBefore(todayLastOrder.getExpectedWaitDatetime())) {
      return LeftTimeResponse.of(between.toMinutes() + 10L);
    } else {
      return LeftTimeResponse.of(10L);
    }
  }

  public void updateOrderStatusToCancel(Long orderId) {
    orderRepository.findById(orderId)
        .ifPresent(order -> order.updateOrderStatus(OrderStatus.CANCELED));
  }
}
