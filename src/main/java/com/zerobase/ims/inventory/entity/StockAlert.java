package com.zerobase.ims.inventory.entity;

import com.zerobase.ims.inventory.entity.Product;
import com.zerobase.ims.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAlert {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToOne
  @JoinColumn(name = "inventory_id", nullable = false)
  private Inventory inventory;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;  // 알림을 받은 사용자 (관리자나 매니저)

  @Column(nullable = false)
  private String message;  // 알림 내용

  @Column(nullable = false)
  private boolean isRead = false;  // 알림을 확인했는지 여부

  @Column(nullable = false)
  private LocalDateTime alertTime = LocalDateTime.now();  // 알림 발생 시간
}
