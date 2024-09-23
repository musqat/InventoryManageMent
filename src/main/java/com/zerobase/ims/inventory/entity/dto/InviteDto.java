package com.zerobase.ims.inventory.entity.dto;

import com.zerobase.ims.common.type.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteDto {

  private String email;  // 초대 받는 유저의 이메일
  private Long inventoryId;  // 초대할 인벤토리 ID
  private Role role;  // 초대할 유저의 역할 (Manager, User)
}
