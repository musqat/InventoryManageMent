package com.zerobase.ims.inventory.service;

import com.zerobase.ims.common.exception.CustomException;
import com.zerobase.ims.common.exception.ErrorCode;
import com.zerobase.ims.common.type.InviteStatus;
import com.zerobase.ims.inventory.entity.Inventory;
import com.zerobase.ims.inventory.entity.InventoryUser;
import com.zerobase.ims.inventory.entity.Invite;
import com.zerobase.ims.inventory.entity.dto.InviteDto;
import com.zerobase.ims.inventory.repository.InventoryRepository;
import com.zerobase.ims.inventory.repository.InventoryUserRepository;
import com.zerobase.ims.inventory.repository.InviteRepository;
import com.zerobase.ims.user.entity.User;
import com.zerobase.ims.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteService {

  private final InviteRepository inviteRepository;
  private final UserRepository userRepository;
  private final InventoryRepository inventoryRepository;
  private final InventoryUserRepository inventoryUserRepository;

  // 유저 초대
  @Transactional
  public InviteDto inviteUser(InviteDto dto) {
    String email = getCurrentUserEmail();

    User inviter = userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

    User invited = userRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

    Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY));

    Invite invite = Invite.builder()
        .inviter(inviter)
        .invite(invited)
        .inventory(inventory)
        .status(InviteStatus.PENDING)
        .invitedAt(LocalDateTime.now())
        .build();

    inviteRepository.save(invite);

    return new InviteDto(dto.getEmail(), dto.getInventoryId(), dto.getRole());
  }

  // 초대 수락
  @Transactional
  public void acceptInvite(Long inviteId) {
    Invite invite = inviteRepository.findById(inviteId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVITER));

    if (invite.getStatus() != InviteStatus.PENDING) {
      throw new CustomException(ErrorCode.INVALID_INVITE_STATUS);
    }

    invite.setStatus(InviteStatus.ACCEPTED);
    invite.setRespondedAt(LocalDateTime.now());

    // 인벤토리-유저 관계 추가 (ADMIN 또는 MEMBER 역할 부여)
    InventoryUser inventoryUser = InventoryUser.builder()
        .user(invite.getInvite())
        .inventory(invite.getInventory())
        .role(invite.getRole())  // 초대 시 지정한 역할
        .build();

    inventoryUserRepository.save(inventoryUser);
  }

  // 초대 거절
  @Transactional
  public void rejectInvite(Long inviteId) {
    Invite invite = inviteRepository.findById(inviteId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVITER));

    if (invite.getStatus() != InviteStatus.PENDING) {
      throw new CustomException(ErrorCode.INVALID_INVITE_STATUS);
    }

    invite.setStatus(InviteStatus.REJECTED);
    invite.setRespondedAt(LocalDateTime.now());
  }

  // 현재 사용자 이메일 반환
  private String getCurrentUserEmail() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
}
