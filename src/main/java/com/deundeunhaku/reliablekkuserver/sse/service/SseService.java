package com.deundeunhaku.reliablekkuserver.sse.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.sse.dto.SseDataResponse;
import com.deundeunhaku.reliablekkuserver.sse.repository.SseInMemoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
public class SseService {

  private final SseInMemoryRepository sseRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public void disconnect(Long orderId) {
    SseEmitter sseEmitter = sseRepository.get(orderId)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    SseDataResponse response = SseDataResponse.of(OrderStatus.CANCELED,
        0L);

    try {
      sseEmitter.send(SseEmitter.event()
          .name("status")
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
            .name("status")
            .data(objectMapper.writeValueAsString(response), APPLICATION_JSON)
        );

      } catch (Exception e) {
        sseEmitter.complete();
      }
    });

  }
}
