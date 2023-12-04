package com.deundeunhaku.reliablekkuserver.order.service;

import com.deundeunhaku.reliablekkuserver.fcm.service.FcmService;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.domain.OfflineMember;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import com.deundeunhaku.reliablekkuserver.member.repository.OfflineMemberRepository;
import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import com.deundeunhaku.reliablekkuserver.menu.repository.MenuRepository;
import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.MenuOrder;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminOrderResponse;
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
import com.deundeunhaku.reliablekkuserver.sse.service.SseService;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final MemberRepository memberRepository;
  private final OfflineMemberRepository offlineMemberRepository;
  private final SseService sseService;
  private final FcmService fcmService;

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

    payment.setOrder(savedOrder);

    List<RegisteredMenuRequest> menuRequestList = request.registeredMenus();

    for (RegisteredMenuRequest menuRequest : menuRequestList) {
      Menu menu = menuRepository.findById(menuRequest.menuId())
          .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
      menuOrderRepository.save(MenuOrder.createMenuOrder(menu, savedOrder, menuRequest.count()));
    }

    Integer allCount = 0;
    for (RegisteredMenuRequest menuRequest : menuRequestList) {
      allCount += menuRequest.count();
    }

    AdminOrderResponse adminOrderResponse = AdminOrderResponse.of(
        savedOrder.getId(),
        savedOrder.getTodayOrderCount(),
        savedOrder.getMember().getPhoneNumber(),
        savedOrder.getOrderDatetime().toLocalTime(),
        savedOrder.getOfflineMember() != null,
        Duration.between(savedOrder.getExpectedWaitDatetime(), savedOrder.getOrderDatetime())
            .toMinutes(),
        allCount,
        menuOrderRepository.findByOrderToOrderEachMenuResponse(savedOrder)
    );
    sseService.sendDataToAdmin(adminOrderResponse);

    fcmService.sendNotificationToAdmin("주문이 들어왔습니다", "빠르게 구워주세요! :)");

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

    boolean isExists = sseService.existsEmitterById(orderId);

    Order order = orderRepository.findById(orderId)
        .orElse(null);

    if (order == null) {
      return null;
    }

    if (isExists) {
      sseService.getEmitter(orderId).complete();
      sseService.removeEmitter(orderId);
    }
    SseEmitter sseEmitter = new SseEmitter();
    log.info("SseEmitter 생성 {}", sseEmitter);

    sseService.saveEmitter(orderId, sseEmitter);

    sseEmitter.onError(e -> {
      log.warn("SseEmitter 에러 발생", e);
      try {
        sseEmitter.send(SseEmitter.event()
            .name("error")
            .data("에러가 발생하였습니다."));
      } catch (IOException ex) {
        log.warn("SseEmitter 에러 발생, 메시지 전송 실패 : orderId : {}", orderId);
        sseEmitter.complete();
      } finally {
        sseService.removeEmitter(orderId);
      }
    });
    sseEmitter.onCompletion(() -> {
      try {
        sseEmitter.send(SseEmitter.event()
            .name("timeout")
            .data("연결이 종료되었습니다."));
      } catch (IOException e) {
        log.warn("SseEmitter 연결 종료 실패 orderId : {}", orderId);
        sseEmitter.complete();
      } finally {
        sseService.removeEmitter(orderId);
      }
    });
    sseEmitter.onTimeout(() -> {
      try {
        sseEmitter.send(SseEmitter.event()
            .name("timeout")
            .data("연결이 종료되었습니다."));
      } catch (IOException e) {
        log.warn("SseEmitter 연결 종료 실패 : orderId : {}", orderId);
        sseEmitter.complete();

      } finally {
        sseService.removeEmitter(orderId);
      }
    });

    try {
      sseEmitter.send(SseEmitter.event()
          .name("open")
          .data("성공!"));
    } catch (IOException e) {
      log.warn("SseEmitter open event 전송 실패 : orderId : {}", orderId);
      sseEmitter.complete();
      sseService.removeEmitter(orderId);
    }

    sseService.sendCookingDataToUser(order);

    return sseEmitter;
  }


  public OrderResponse getOrderMenuList(Long orderId, Member member) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    List<MenuOrder> menuOrderList = menuOrderRepository.findByOrder(order);

    List<OrderEachMenuResponse> menuResponseList = menuOrderList.stream()
        .map(menuOrder -> {
          Menu menu = menuOrder.getMenu();
          return OrderEachMenuResponse.of(menu.getName(), menuOrder.getCount());
        })
        .collect(Collectors.toList());

    return OrderResponse.of(member.getRealName(), order.getOrderPrice(), menuResponseList);
  }

  @Transactional
  public void deleteOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    if (order.getOrderStatus().equals(OrderStatus.COOKING)) {
      throw new IllegalArgumentException("이미 접수된 주문입니다.");
    }

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
    List<OrderStatus> orderProceedingStatus = List.of(OrderStatus.WAIT, OrderStatus.COOKING,
        OrderStatus.PICKUP);

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

    List<Order> orders = orderRepository.findOrderListByMemberAndCreatedDateBetweenAndOrderStatusNotIn(
        member,
        firstDate,
        lastDate,
        Set.of(OrderStatus.CANCELED));

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
          log.info("order.getCreatedAt() : {}", order.getCreatedAt());
          return PastOrderResponse.of(order.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")),
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

  @Transactional(readOnly = true)
  public LeftTimeResponse getLeftTime() {
    LocalDateTime nowDateTime = LocalDateTime.now();
    ZonedDateTime nowSeoulDateTime = ZonedDateTime.of(nowDateTime, ZoneId.of("Asia/Seoul"));

    Order todayLastOrder = orderRepository.findFirstByCreatedDateAndOrderStatusNotInOrderByCreatedAtDesc(
            nowSeoulDateTime.toLocalDate(), List.of(OrderStatus.CANCELED, OrderStatus.PICKUP))
        .orElse(Order.builder().expectedWaitDatetime(nowDateTime).build());

    Duration between = Duration.between(nowSeoulDateTime, todayLastOrder.getExpectedWaitDatetime());
    log.info("between : {}", between.toMinutes());
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

  public Optional<Order> findOptionalByOrderId(Long orderId) {
    return orderRepository.findById(orderId);
  }
}
