package com.zerobase.ims.inventory.entity;

import com.zerobase.ims.common.type.Role;
import com.zerobase.ims.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(
    name = "inventory_user",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "inventory_id"})
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class InventoryUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Role role;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_id", nullable = false)
  private Inventory inventory;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "inventoryUser", cascade = CascadeType.ALL)
  private Set<Category> categories = new HashSet<>();

  @OneToMany(mappedBy = "inventoryUser", cascade = CascadeType.ALL)
  private Set<Product> products = new HashSet<>();

}
