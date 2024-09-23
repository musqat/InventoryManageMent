package com.zerobase.ims.common;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseEmitterService {

  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  public SseEmitter createEmitter(String userId) {
    SseEmitter emitter = new SseEmitter(0L);  // 타임아웃 없음
    emitters.put(userId, emitter);
    emitter.onCompletion(() -> emitters.remove(userId));
    emitter.onTimeout(() -> emitters.remove(userId));
    return emitter;
  }

  public void sendStockUpdateNotification(String userId, String message) {
    SseEmitter emitter = emitters.get(userId);
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event().name("재고-업데이트").data(message));
      } catch (IOException e) {
        emitters.remove(userId);
      }
    }
  }
}
