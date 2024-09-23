package com.zerobase.ims.inventory.entity.dto;

import com.zerobase.ims.inventory.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
  private Long id;
  private String name;
  private String description;
  private int price;
  private int quantity;
  private int minquantity;
  private String supplier;

  public static ProductResponse of(Product product) {
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .quantity(product.getQuantity())
        .minquantity(product.getMinquantity())
        .supplier(product.getSupplier())
        .build();
  }

}
