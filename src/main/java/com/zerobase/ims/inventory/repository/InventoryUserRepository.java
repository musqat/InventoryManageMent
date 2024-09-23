package com.zerobase.ims.inventory.repository;

import com.zerobase.ims.inventory.entity.InventoryUser;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryUserRepository extends JpaRepository<InventoryUser, Long> {
  Optional<InventoryUser> findByUserIdAndInventoryId(Long userId, Long inventoryId);
  Optional<InventoryUser> findByUserEmailAndInventoryId(String email, Long inventoryId);

  @Query("SELECT iu FROM InventoryUser iu WHERE iu.inventory.id = :inventoryId AND (iu.role = 'ADMIN' OR iu.role = 'MANAGER')")
  List<InventoryUser> findAdminsAndManagersByInventory(@Param("inventoryId") Long inventoryId);

}
