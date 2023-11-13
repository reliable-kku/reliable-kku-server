package com.deundeunhaku.reliablekkuserver.sse.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminOrderResponse;
import com.deundeunhaku.reliablekkuserver.sse.dto.SseDataResponse;
import com.deundeunhaku.reliablekkuserver.sse.repository.SseInMemoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseService {

  private final SseInMemoryRepository sseRepository;
  private final ObjectMapper objectMapper;


  public SseEmitter getEmitter(Long id) {
    SseEmitter sseEmitter = sseRepository.get(id)
        .orElse(null);

    if (sseEmitter == null) {
      log.warn("SseEmitter가 존재하지 않습니다. orderId: {}", id);
      return null;
    }

    return sseEmitter;
  }

  public boolean existsEmitterById(Long orderId) {
    Optional<SseEmitter> sseEmitter = sseRepository.get(orderId);
    return sseEmitter.isPresent();
  }

  public void saveEmitter(Long orderId, SseEmitter sseEmitter) {
    sseRepository.put(orderId, sseEmitter);
  }

  public void removeEmitter(Long orderId) {
    sseRepository.remove(orderId);
  }

  public void disconnect(Long orderId) {
    SseEmitter sseEmitter = sseRepository.get(orderId)
        .orElse(null);

    if (sseEmitter == null) {
      log.warn("주문 취소,  SseEmitter가 존재하지 않습니다. orderId: {}", orderId);
      return;
    }

    SseDataResponse response = SseDataResponse.of(OrderStatus.CANCELED,
        0L);

    try {
      sseEmitter.send(SseEmitter.event()
          .name("message")
          .data(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

      sseRepository.remove(orderId);
    } catch (Exception e) {
      log.warn("주문 취소, SseEmitter 메시지 전송 실패, orderId: {}", orderId);
    }
  }

  public void sendCookingDataToUser(Order order) {

    Duration leftDuration = Duration.between(LocalDateTime.now(),
        order.getExpectedWaitDatetime());

    SseDataResponse response = SseDataResponse.of(
        order.getOrderStatus(),
        leftDuration.toMinutes()
    );

    sseRepository.get(order.getId()).ifPresent(sseEmitter -> {
      try {
        sseEmitter.send(SseEmitter.event()
            .name("message")
            .data(objectMapper.writeValueAsString(response), APPLICATION_JSON)
        );
    log.info("전달 성공 orderId: {}", order.getId());
      } catch (Exception e) {
        log.warn("SseEmitter 메시지 전송 실패 orderId: {}", order.getId());
//        sseEmitter.complete();
      }
    });
  }

  public void sendDataToUser(Long orderId, OrderStatus orderStatus, Long leftMinutes) {

    SseDataResponse response = SseDataResponse.of(orderStatus,
        leftMinutes);

    sseRepository.get(orderId).ifPresent(sseEmitter -> {
      try {
        sseEmitter.send(SseEmitter.event()
            .name("message")
            .data(objectMapper.writeValueAsString(response), APPLICATION_JSON)
        );

      } catch (Exception e) {
        sseEmitter.complete();
      }
    });

  }

  public void sendDataToAdmin(AdminOrderResponse adminOrderResponse) {

    SseEmitter emitter = getEmitter(0L);

    if (emitter == null) {
      log.warn("관리자 SSEEmitter가 존재하지 않습니다.");
      return;
    }

    try {
      emitter.send(SseEmitter.event()
          .name("message")
          .data(objectMapper.writeValueAsString(adminOrderResponse), APPLICATION_JSON)
      );
    } catch (Exception e) {
      log.warn("관리자 SSEEmitter 메시지 전송 실패");
      emitter.complete();
    }
  }

  public Map<Long, SseEmitter> getAllEmitter() {
     return sseRepository.getAll();
  }
}
