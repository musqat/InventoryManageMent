package com.zerobase.ims.inventory.service;

import com.zerobase.ims.common.exception.CustomException;
import com.zerobase.ims.common.exception.ErrorCode;
import com.zerobase.ims.common.type.Role;
import com.zerobase.ims.inventory.entity.Inventory;
import com.zerobase.ims.inventory.entity.InventoryUser;
import com.zerobase.ims.inventory.entity.dto.InventoryDto;
import com.zerobase.ims.inventory.entity.dto.InventoryResponse;
import com.zerobase.ims.inventory.entity.dto.InviteDto;
import com.zerobase.ims.inventory.repository.InventoryRepository;
import com.zerobase.ims.inventory.repository.InventoryUserRepository;
import com.zerobase.ims.user.entity.User;
import com.zerobase.ims.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InventoryService {

  private final InventoryRepository inventoryRepository;
  private final InventoryUserRepository inventoryUserRepository;
  private final UserRepository userRepository;

  // 현재 인증된 사용자 이메일 가져오기
  private String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName();
  }

  @Transactional
  public List<InventoryResponse> getAllInventories() {
    List<Inventory> inventories = inventoryRepository.findAll();
    return inventories.stream()
        .map(inventory -> new InventoryResponse(inventory.getId(), inventory.getName(), inventory.getDescription()))
        .collect(Collectors.toList());
  }


  @Transactional
  public InventoryResponse createInventory(InventoryDto dto) {
    String email = getCurrentUserEmail();

    // 이메일로 사용자 조회
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

    // 인벤토리 생성 및 저장
    Inventory inventory = new Inventory();
    inventory.setName(dto.getName());
    inventory.setDescription(dto.getDescription());
    inventoryRepository.save(inventory);

    // InventoryUser 생성 및 저장 (관리자로 등록)
    InventoryUser inventoryUser = InventoryUser.builder()
        .inventory(inventory)
        .user(user)
        .role(Role.ADMIN)  // 관리자 역할 설정
        .build();
    inventoryUserRepository.save(inventoryUser);

    return new InventoryResponse(inventory.getId(), inventory.getName(), inventory.getDescription());
  }

  @Transactional
  public InventoryResponse updateInventory(Long inventoryId, InventoryDto dto) {
    String email = getCurrentUserEmail();

    // 사용자 정보 및 권한 확인
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

    InventoryUser inventoryUser = inventoryUserRepository
        .findByUserIdAndInventoryId(user.getId(), inventoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY_USER));

    if (!InventoryAuth(inventoryUser)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    // 인벤토리 수정
    Inventory inventory = inventoryRepository.findById(inventoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY));
    inventory.setName(dto.getName());
    inventory.setDescription(dto.getDescription());
    inventoryRepository.save(inventory);

    return new InventoryResponse(inventory.getId(), inventory.getName(), inventory.getDescription());
  }

  @Transactional
  public void deleteInventory(Long inventoryId) {
    String email = getCurrentUserEmail();

    // 사용자 정보 및 권한 확인
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

    InventoryUser inventoryUser = inventoryUserRepository
        .findByUserIdAndInventoryId(user.getId(), inventoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

    if (!InventoryAuth(inventoryUser)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    // 인벤토리 삭제
    inventoryRepository.deleteById(inventoryId);
  }

  @Transactional
  public void inviteUser(InviteDto inviteDto) {
    String inviterEmail = getCurrentUserEmail();

    // 초대하는 유저 확인
    User inviter = userRepository.findByEmail(inviterEmail)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

    InventoryUser inviterInventoryUser = inventoryUserRepository.findByUserIdAndInventoryId(
            inviter.getId(), inviteDto.getInventoryId())
        .orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

    if (!InventoryAuth(inviterInventoryUser)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    // 초대받을 유저 확인
    User invitedUser = userRepository.findByEmail(inviteDto.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

    // 초대하려는 유저를 인벤토리에 추가
    InventoryUser inventoryUser = new InventoryUser();
    inventoryUser.setUser(invitedUser);
    inventoryUser.setInventory(inventoryRepository.findById(inviteDto.getInventoryId())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY)));
    inventoryUser.setRole(inviteDto.getRole());

    inventoryUserRepository.save(inventoryUser);
  }

  private boolean InventoryAuth(InventoryUser inventoryUser) {
    return inventoryUser.getRole() == Role.ADMIN || inventoryUser.getRole() == Role.MANAGER;
  }
}
