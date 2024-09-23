package com.zerobase.ims.inventory.service;

import com.zerobase.ims.common.exception.CustomException;
import com.zerobase.ims.common.exception.ErrorCode;
import com.zerobase.ims.common.type.Role;
import com.zerobase.ims.inventory.entity.Category;
import com.zerobase.ims.inventory.entity.InventoryUser;
import com.zerobase.ims.inventory.entity.dto.CategoryDto;
import com.zerobase.ims.inventory.entity.dto.CategoryResponse;
import com.zerobase.ims.inventory.repository.CategoryRepository;
import com.zerobase.ims.inventory.repository.InventoryUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final InventoryUserRepository inventoryUserRepository;

  // 카테고리 생성
  @Transactional
  public CategoryResponse createCategory(Long inventoryId, CategoryDto dto) {
    String email = getCurrentUserEmail();

    InventoryUser inventoryUser = inventoryUserRepository.findByUserEmailAndInventoryId(email, inventoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY_USER));

    if (!CategoryAuth(inventoryUser)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    Category category = new Category();
    category.setName(dto.getName());
    category.setDescription(dto.getDescription());
    categoryRepository.save(category);

    return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
  }

  // 카테고리 수정
  @Transactional
  public CategoryResponse updateCategory(Long categoryId, CategoryDto dto) {
    String email = getCurrentUserEmail();

    InventoryUser inventoryUser = inventoryUserRepository.findByUserEmailAndInventoryId(email, categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY_USER));

    if (!CategoryAuth(inventoryUser)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CATEGORY));

    category.setName(dto.getName());
    category.setDescription(dto.getDescription());
    categoryRepository.save(category);

    return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
  }

  // 카테고리 삭제
  @Transactional
  public void deleteCategory(Long inventoryId, Long categoryId) {
    String email = getCurrentUserEmail();

    InventoryUser inventoryUser = inventoryUserRepository.findByUserEmailAndInventoryId(email, inventoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY_USER));

    if (!CategoryAuth(inventoryUser)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CATEGORY));

    categoryRepository.delete(category);
  }

  // 전체 카테고리 조회
  public List<CategoryResponse> getAllCategories(Long inventoryId) {
    List<Category> categories = categoryRepository.findByInventoryId(inventoryId);
    return categories.stream()
        .map(category -> new CategoryResponse(category.getId(), category.getName(), category.getDescription()))
        .collect(Collectors.toList());
  }

  // 권한 확인
  private boolean CategoryAuth(InventoryUser inventoryUser) {
    return inventoryUser.getRole() == Role.ADMIN || inventoryUser.getRole() == Role.MANAGER;
  }

  // 현재 사용자 이메일 반환
  private String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName();
  }
}
