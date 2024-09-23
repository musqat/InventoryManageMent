package com.zerobase.ims.inventory.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

  private String name;
  private String description;
  private int price;
  private int quantity;
  private int minquantity;  // 변수명 일관성 있게 수정
  private String supplier;
  private Long inventoryId;  // Inventory의 ID를 참조하는 필드
}

