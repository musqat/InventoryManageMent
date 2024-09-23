package com.zerobase.ims.inventory.controller;

import com.zerobase.ims.inventory.entity.dto.InviteDto;
import com.zerobase.ims.inventory.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invite")
@RequiredArgsConstructor
public class InviteController {

  private final InviteService inviteService;

  // 유저 초대
  @PostMapping("/invite")
  public ResponseEntity<String> inviteUser(@RequestBody InviteDto inviteDto) {
    inviteService.inviteUser(inviteDto);
    return ResponseEntity.ok("초대가 발송되었습니다.");
  }

  // 초대 수락
  @PostMapping("/accept/{inviteId}")
  public ResponseEntity<String> acceptInvite(@PathVariable Long inviteId) {
    inviteService.acceptInvite(inviteId);
    return ResponseEntity.ok("초대를 수락했습니다.");
  }

  // 초대 거절
  @PostMapping("/reject/{inviteId}")
  public ResponseEntity<String> rejectInvite(@PathVariable Long inviteId) {
    inviteService.rejectInvite(inviteId);
    return ResponseEntity.ok("초대를 거절했습니다.");
  }
}
