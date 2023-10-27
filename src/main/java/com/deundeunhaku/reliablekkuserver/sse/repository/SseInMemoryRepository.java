package com.deundeunhaku.reliablekkuserver.sse.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
public class SseInMemoryRepository implements SSERepository{
  private final Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

  @Override
  public void put(Long key, SseEmitter sseEmitter) {
    sseEmitterMap.put(key, sseEmitter);
  }
  @Override
  public Optional<SseEmitter> get(Long key) {
    return Optional.ofNullable(sseEmitterMap.get(key));
  }

  @Override
  public void remove(Long key) {
    sseEmitterMap.remove(key);
  }
}