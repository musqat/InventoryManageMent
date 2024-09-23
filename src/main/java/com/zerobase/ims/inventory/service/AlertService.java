package com.zerobase.ims.inventory.service;

import com.zerobase.ims.common.exception.CustomException;
import com.zerobase.ims.common.exception.ErrorCode;
import com.zerobase.ims.inventory.entity.StockAlert;
import com.zerobase.ims.inventory.repository.AlertRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertService {

  private final AlertRepository alertRepository;

  public List<StockAlert> getUserAlerts(Long userId) {
    return alertRepository.findByUserId(userId);
  }

  public void AlertRead(Long alertId) {
    StockAlert alert = alertRepository.findById(alertId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ALERT));
    alert.setRead(true);
    alertRepository.save(alert);
  }
}