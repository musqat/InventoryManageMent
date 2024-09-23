package com.zerobase.ims.inventory.repository;

import com.zerobase.ims.inventory.entity.ProductHistory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Long> {
  List<ProductHistory> findByProductIdAndChangeDateBetween(Long productId, LocalDate startDate, LocalDate endDate);

}
