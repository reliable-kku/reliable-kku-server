package com.deundeunhaku.reliablekkuserver.scheduler;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.service.OrderService;
import com.deundeunhaku.reliablekkuserver.sse.service.SseService;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class SchedulerService {

  private final SseService sseService;
  private final OrderService orderService;

  @Scheduled(fixedDelay = 50000)
  public void sendLeftTimeWithSseToClient() {
    Map<Long, SseEmitter> allEmitter = sseService.getAllEmitter();
    log.info("sse 남은시간 전달 scheduler 실행 orderId 리스트 : {}", allEmitter.keySet());

    for (Long id : allEmitter.keySet()) {
      if (id.equals(0L)){
        return;
      }

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
