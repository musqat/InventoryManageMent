package com.zerobase.ims.inventory.repository;

import com.zerobase.ims.inventory.entity.StockAlert;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<StockAlert, Long> {

  List<StockAlert> findByUserId(Long userId);
}
