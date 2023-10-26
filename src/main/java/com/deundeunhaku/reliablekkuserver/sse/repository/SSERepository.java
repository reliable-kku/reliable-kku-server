package com.deundeunhaku.reliablekkuserver.sse.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public interface SSERepository {

  public void put(Long key, SseEmitter sseEmitter);

  public Optional<SseEmitter> get(Long key);

  public void remove(Long key);

}
