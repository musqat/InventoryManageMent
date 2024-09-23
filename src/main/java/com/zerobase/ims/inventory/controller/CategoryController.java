package com.zerobase.ims.inventory.controller;

import com.zerobase.ims.inventory.entity.dto.CategoryDto;
import com.zerobase.ims.inventory.entity.dto.CategoryResponse;
import com.zerobase.ims.inventory.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/inventory/{inventoryId}/category")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  // 카테고리 전체 조회
  @GetMapping("/")
  public ResponseEntity<List<CategoryResponse>> getAllCategories(@PathVariable("inventoryId") Long inventoryId) {
    List<CategoryResponse> categories = categoryService.getAllCategories(inventoryId);
    return ResponseEntity.ok(categories);
  }

  // 카테고리 생성
  @PostMapping("/create")
  public ResponseEntity<CategoryResponse> createCategory(
      @PathVariable("inventoryId") Long inventoryId,
      @RequestBody CategoryDto dto) {
    CategoryResponse response = categoryService.createCategory(inventoryId, dto);
    return ResponseEntity.ok(response);
  }

  // 카테고리 수정
  @PutMapping("/update/{categoryId}")
  public ResponseEntity<CategoryResponse> updateCategory(
      @PathVariable("inventoryId") Long inventoryId,
      @PathVariable("categoryId") Long categoryId,
      @RequestBody CategoryDto dto) {
    CategoryResponse response = categoryService.updateCategory(categoryId, dto);
    return ResponseEntity.ok(response);
  }

  // 카테고리 삭제
  @DeleteMapping("/delete/{categoryId}")
  public ResponseEntity<String> deleteCategory(
      @PathVariable("inventoryId") Long inventoryId,
      @PathVariable("categoryId") Long categoryId) {
    categoryService.deleteCategory(inventoryId,
        categoryId);
    return ResponseEntity.ok("카테고리가 삭제되었습니다.");
  }
}
