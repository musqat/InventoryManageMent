package com.zerobase.ims.inventory.controller;

import com.zerobase.ims.common.SseEmitterService;
import com.zerobase.ims.inventory.entity.StockAlert;
import com.zerobase.ims.inventory.service.AlertService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

  private final AlertService alertService;
  private final SseEmitterService sseEmitterService;

  @GetMapping("/notifications/{userId}")
  public SseEmitter connectToNotifications(@PathVariable String userId) {
    return sseEmitterService.createEmitter(userId);
  }

  // 알림 목록 조회 (사용자별)
  @GetMapping
  public List<StockAlert> getUserAlerts(@RequestParam("userId") Long userId) {
    return alertService.getUserAlerts(userId);
  }

  // 알림 확인 처리
  @PostMapping("/{alertId}/read")
  public ResponseEntity<Void> markAlertAsRead(@PathVariable Long alertId) {
    alertService.AlertRead(alertId);
    return ResponseEntity.ok().build();
  }
}
