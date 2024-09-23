package com.zerobase.ims.inventory.repository;

import com.zerobase.ims.inventory.entity.Product;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<Product, Long> {

  // 제품 이름으로 검색
  List<Product> findByName(String name);

  // 제품 설명에서 특정 단어를 포함하는 제품 검색
  List<Product> findByDescriptionContaining(String description);

  // 가격 범위 내의 제품 검색
  List<Product> findByPriceBetween(int minPrice, int maxPrice);

  // 재고 범위 내의 제품 검색
  List<Product> findByQuantityBetween(int minQuantity, int maxQuantity);


}
