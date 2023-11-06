package com.deundeunhaku.reliablekkuserver.sse.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminOrderResponse;
import com.deundeunhaku.reliablekkuserver.sse.dto.SseDataResponse;
import com.deundeunhaku.reliablekkuserver.sse.repository.SseInMemoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  @Transactional
  public void saveEmitter(Long orderId, SseEmitter sseEmitter) {
    sseRepository.put(orderId, sseEmitter);
  }

  @Transactional
  public void removeEmitter(Long orderId) {
    sseRepository.remove(orderId);
  }

  @Transactional
  public void disconnect(Long orderId) {
    SseEmitter sseEmitter = sseRepository.get(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    SseDataResponse response = SseDataResponse.of(OrderStatus.CANCELED,
        0L);

    try {
      sseEmitter.send(SseEmitter.event()
          .name("message")
          .data(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

      sseRepository.remove(orderId);
    }catch (Exception e){
      throw new IllegalArgumentException("잘못된 요청입니다.");
    }
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
      return;
    }

    try {
      emitter.send(SseEmitter.event()
          .name("message")
          .data(objectMapper.writeValueAsString(adminOrderResponse), APPLICATION_JSON)
      );
    } catch (Exception e) {
      emitter.complete();
    }
  }
}
