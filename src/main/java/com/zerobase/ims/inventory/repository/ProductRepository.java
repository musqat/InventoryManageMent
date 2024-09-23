package com.zerobase.ims.inventory.repository;

import com.zerobase.ims.inventory.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

  Optional<Product> findByName(String name);
  List<Product> findByInventoryIdAndCategoryId(Long inventoryId, Long categoryId);

}
