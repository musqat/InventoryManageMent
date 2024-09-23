package com.zerobase.ims.inventory.controller;

import com.zerobase.ims.inventory.entity.dto.InventoryDto;
import com.zerobase.ims.inventory.entity.dto.InventoryResponse;
import com.zerobase.ims.inventory.entity.dto.InviteDto;
import com.zerobase.ims.inventory.service.InventoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

  @Autowired
  private InventoryService inventoryService;

  // 인벤토리 전체 조회
  @GetMapping("/")
  public ResponseEntity<List<InventoryResponse>> getAllInventories() {
    List<InventoryResponse> inventories = inventoryService.getAllInventories();
    return ResponseEntity.ok(inventories);
  }


  // 인벤토리 생성
  @PostMapping("/create")
  public ResponseEntity<?> createInventory(@RequestBody InventoryDto dto) {
    inventoryService.createInventory(dto);
    return ResponseEntity.ok("인벤토리가 생성되었습니다.");
  }

  // 인벤토리 수정
  @PutMapping("/update/{inventoryId}")
  public ResponseEntity<?> updateInventory(@PathVariable Long inventoryId,
      @RequestBody InventoryDto dto) {
    inventoryService.updateInventory(inventoryId, dto);
    return ResponseEntity.ok("인벤토리가 수정되었습니다.");
  }

  // 인벤토리 삭제
  @DeleteMapping("/{inventoryId}")
  public ResponseEntity<?> deleteInventory(@PathVariable Long inventoryId) {
    inventoryService.deleteInventory(inventoryId);
    return ResponseEntity.ok("인벤토리가 삭제되었습니다.");
  }

  // 유저 초대
  @PostMapping("/invite")
  public ResponseEntity<String> inviteUser(@RequestBody InviteDto inviteDto) {
    inventoryService.inviteUser(inviteDto);
    return ResponseEntity.ok("초대가 발송되었습니다.");
  }
}
