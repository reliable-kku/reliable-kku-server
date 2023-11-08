package com.deundeunhaku.reliablekkuserver.scheduler;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.service.OrderService;
import com.deundeunhaku.reliablekkuserver.sse.service.SseService;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
public class SchedulerService {

  private final SseService sseService;
  private final OrderService orderService;

  @Scheduled(fixedDelay = 10000)
  public void sendLeftTimeWithSseToClient() {
    Map<Long, SseEmitter> allEmitter = sseService.getAllEmitter();

    for (Long id : allEmitter.keySet()) {
      Optional<Order> optionalOrder = orderService.findOptionalByOrderId(id);
      if (optionalOrder.isEmpty()) {
        return;
      }
      Order order = optionalOrder.get();

      if (order.getOrderStatus().equals(OrderStatus.COOKING)) {
        sseService.sendCookingDataToUser(order);
      }
    }
  }


}
