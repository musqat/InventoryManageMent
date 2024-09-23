package com.zerobase.ims.inventory.controller;

import com.zerobase.ims.inventory.entity.dto.ProductDto;
import com.zerobase.ims.inventory.entity.dto.ProductResponse;
import com.zerobase.ims.inventory.service.ProductService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory/{inventoryId}/category/{categoryId}product")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping("/")
  public ResponseEntity<List<ProductResponse>> getAllProducts(@PathVariable Long inventoryId,
      @PathVariable Long categoryId) {
    List<ProductResponse> products = productService.getProductsByInventoryAndCategory(inventoryId,
        categoryId);
    return ResponseEntity.ok(products);
  }

  // 제품 생성
  @PostMapping("/create")
  public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductDto productDto,
      @PathVariable Long inventoryId) {
    ProductResponse product = productService.createProduct(productDto, inventoryId);
    return ResponseEntity.ok(product);
  }

  // 제품 수정
  @PutMapping("/{productId}/update")
  public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId,
      @RequestBody ProductDto productDto, @PathVariable Long inventoryId) {
    ProductResponse product = productService.updateProduct(productId, productDto, inventoryId);
    return ResponseEntity.ok(product);
  }

  // 제품 삭제
  @DeleteMapping("/{productId}/delete")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long productId,
      @PathVariable Long inventoryId) {
    productService.deleteProduct(productId, inventoryId);
    return ResponseEntity.ok().build();
  }

  // 재고 조정
  @PostMapping("/adjust")
  public ResponseEntity<Void> adjustStock(@RequestBody ProductDto productDto) {
    productService.adjustStock(productDto);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/special-adjust")
  public ResponseEntity<Void> specialAdjustStock(
      @PathVariable Long productId, @RequestParam int newQuantity,
      @RequestParam String reason, @PathVariable Long inventoryId) {
    productService.specialAdjustStock(productId, newQuantity, reason, inventoryId);
    return ResponseEntity.ok().build();
  }

  // 이름으로 제품 검색
  @GetMapping("/search/name")
  public ResponseEntity<List<ProductResponse>> searchProductsByName(@RequestParam String name) {
    List<ProductResponse> products = productService.searchProductsByName(name);
    return ResponseEntity.ok(products);
  }

  // 설명으로 제품 검색
  @GetMapping("/search/description")
  public ResponseEntity<List<ProductResponse>> searchProductsByDescription(
      @RequestParam String description) {
    List<ProductResponse> products = productService.searchProductsByDescription(description);
    return ResponseEntity.ok(products);
  }

  // 가격 범위로 제품 검색
  @GetMapping("/search/price")
  public ResponseEntity<List<ProductResponse>> searchProductsByPriceRange(
      @RequestParam int minPrice, @RequestParam int maxPrice) {
    List<ProductResponse> products = productService.searchProductsByPriceRange(minPrice, maxPrice);
    return ResponseEntity.ok(products);
  }

  // 수량 범위로 제품 검색
  @GetMapping("/search/quantity")
  public ResponseEntity<List<ProductResponse>> searchProductsByQuantityRange(
      @RequestParam int minQuantity, @RequestParam int maxQuantity) {
    List<ProductResponse> products = productService.searchProductsByQuantityRange(minQuantity,
        maxQuantity);
    return ResponseEntity.ok(products);
  }

  // 엑셀 파일로 재고 변동 내역 다운로드
  @GetMapping("/export")
  public ResponseEntity<byte[]> exportExcel(
      @PathVariable Long productId,
      @RequestParam String directoryPath,
      @RequestParam String startDate,
      @RequestParam String endDate) {

    try {
      LocalDate start = LocalDate.parse(startDate);
      LocalDate end = LocalDate.parse(endDate);

      // 엑셀 파일 생성 및 저장, 경로 반환
      String filePath = productService.exportExcel(productId, directoryPath, start, end);

      // 파일 읽어오기
      Path path = Paths.get(filePath);
      byte[] fileData = Files.readAllBytes(path);

      // 파일 다운로드를 위한 헤더 설정
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=" + path.getFileName().toString());

      return ResponseEntity.ok()
          .headers(headers)
          .body(fileData);

    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}


