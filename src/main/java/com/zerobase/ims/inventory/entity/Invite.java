package com.zerobase.ims.inventory.entity;

import com.zerobase.ims.common.type.InviteStatus;
import com.zerobase.ims.common.type.Role;
import com.zerobase.ims.inventory.entity.Inventory;
import com.zerobase.ims.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Invite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @ManyToOne
  @JoinColumn(name = "inviter_id")
  private User inviter;

  @ManyToOne
  @JoinColumn(name = "invitee_id")
  private User invite;

  @ManyToOne
  @JoinColumn(name = "inventory_id")
  private Inventory inventory;

  @Enumerated(EnumType.STRING)
  private InviteStatus status;

  private LocalDateTime invitedAt;
  private LocalDateTime respondedAt;
}
