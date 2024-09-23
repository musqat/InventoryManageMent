package com.zerobase.ims.inventory.entity;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String productName;

  @Column(nullable = false)
  private int oldQuantity;

  @Column(nullable = false)
  private int newQuantity;

  private String changeReason;

  @Column(nullable = false)
  private String changedByEmail;

  @Column(nullable = false)
  private LocalDateTime changeDate = LocalDateTime.now();

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

}
