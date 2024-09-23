package com.zerobase.ims.inventory.repository;

import com.zerobase.ims.inventory.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  List<Category> findByInventoryId(Long inventoryId);

}
