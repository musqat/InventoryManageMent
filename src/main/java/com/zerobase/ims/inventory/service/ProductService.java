package com.zerobase.ims.inventory.service;

import com.zerobase.ims.common.SseEmitterService;
import com.zerobase.ims.common.exception.CustomException;
import com.zerobase.ims.common.exception.ErrorCode;
import com.zerobase.ims.common.type.Role;
import com.zerobase.ims.inventory.entity.Category;
import com.zerobase.ims.inventory.entity.ExcelLog;
import com.zerobase.ims.inventory.entity.Inventory;
import com.zerobase.ims.inventory.entity.InventoryUser;
import com.zerobase.ims.inventory.entity.Product;
import com.zerobase.ims.inventory.entity.ProductHistory;
import com.zerobase.ims.inventory.entity.StockAlert;
import com.zerobase.ims.inventory.entity.dto.ProductDto;
import com.zerobase.ims.inventory.entity.dto.ProductResponse;
import com.zerobase.ims.inventory.repository.AlertRepository;
import com.zerobase.ims.inventory.repository.ExcelLogRepository;
import com.zerobase.ims.inventory.repository.InventoryUserRepository;
import com.zerobase.ims.inventory.repository.ProductHistoryRepository;
import com.zerobase.ims.inventory.repository.ProductRepository;
import com.zerobase.ims.inventory.repository.ProductSearchRepository;
import com.zerobase.ims.user.entity.User;
import jakarta.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductSearchRepository productSearchRepository;
  private final ProductHistoryRepository productHistoryRepository;
  private final InventoryUserRepository inventoryUserRepository;
  private final AlertRepository alertRepository;
  private final SseEmitterService sseEmitterService;
  private final ExcelLogRepository excelLogRepository;


  // 전체 조회
  public List<ProductResponse> getProductsByInventoryAndCategory(Long inventoryId,
      Long categoryId) {
    List<Product> products = productRepository.findByInventoryIdAndCategoryId(inventoryId,
        categoryId);
    return products.stream()
        .map(ProductResponse::of)
        .collect(Collectors.toList());
  }

  // 제품 생성
  @Transactional
  public ProductResponse createProduct(ProductDto productDto, Long inventoryId) {
    String email = getCurrentUserEmail();

    InventoryUser inventoryUser = inventoryUserRepository.findByUserEmailAndInventoryId(email,
            inventoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY_USER));

    if (!hasRole(inventoryUser)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    Product product = Product.builder()
        .name(productDto.getName())
        .description(productDto.getDescription())
        .price(productDto.getPrice())
        .quantity(productDto.getQuantity())
        .minquantity(productDto.getMinquantity())
        .supplier(productDto.getSupplier())
        .build();

    productRepository.save(product);
    productSearchRepository.save(product);

    return ProductResponse.of(product);
  }

  // 제품 수정
  @Transactional
  public ProductResponse updateProduct(Long productId, ProductDto productDto, Long inventoryId) {
    String email = getCurrentUserEmail();
    InventoryUser inventoryUser = inventoryUserRepository.findByUserEmailAndInventoryId(email,
            inventoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY_USER));

    if (!hasRole(inventoryUser)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

    product.setName(productDto.getName());
    product.setDescription(productDto.getDescription());
    product.setPrice(productDto.getPrice());
    product.setQuantity(productDto.getQuantity());
    product.setMinquantity(productDto.getMinquantity());
    product.setSupplier(productDto.getSupplier());

    productRepository.save(product);
    productSearchRepository.save(product);

    return ProductResponse.of(product);
  }

  // 제품 삭제
  @Transactional
  public void deleteProduct(Long productId, Long inventoryId) {
    String email = getCurrentUserEmail();
    InventoryUser inventoryUser = inventoryUserRepository.findByUserEmailAndInventoryId(email,
            inventoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY_USER));

    if (!hasRole(inventoryUser)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

    productRepository.delete(product);
    productSearchRepository.delete(product);
  }

  // 재고 조정
  @Transactional
  public void adjustStock(ProductDto productDto) {
    String email = getCurrentUserEmail();
    InventoryUser inventoryUser = inventoryUserRepository.findByUserEmailAndInventoryId(email,
            productDto.getInventoryId())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY_USER));

    if (!isInventoryUser(inventoryUser)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    Product product = productRepository.findByName(productDto.getName())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

    int oldQuantity = product.getQuantity();
    product.setQuantity(productDto.getQuantity());
    productRepository.save(product);
    productSearchRepository.save(product);

    productHistoryRepository.save(ProductHistory.builder()
        .product(product)
        .productName(product.getName())
        .oldQuantity(oldQuantity)
        .newQuantity(productDto.getQuantity())
        .changedByEmail(email)
        .build());

    // 최소 수량 미만일 경우 관리자 알림 전송
    if (product.getQuantity() < product.getMinquantity()) {
      String inventoryName = product.getCategory().getInventory().getName();
      String categoryName = product.getCategory().getName();
      String message = String.format("%s 인벤토리의 %s 카테고리의 %s 제품의 개수가 최소수량 미만입니다.",
          inventoryName, categoryName, product.getName());

      List<InventoryUser> managersAndAdmins = inventoryUserRepository.findAdminsAndManagersByInventory(
          productDto.getInventoryId());
      for (InventoryUser managerOrAdmin : managersAndAdmins) {
        StockAlert alert = StockAlert.builder()
            .product(product)
            .user(managerOrAdmin.getUser())
            .message(message)
            .build();
        alertRepository.save(alert);

        sseEmitterService.sendStockUpdateNotification(managerOrAdmin.getUser().getId().toString(),
            message);
      }
    }
  }

  // 특수 재고 조정
  @Transactional
  public ProductResponse specialAdjustStock(Long productId, int newQuantity,
      String reason, Long inventoryId) {
    String email = getCurrentUserEmail();

    InventoryUser inventoryUser = inventoryUserRepository.findByUserEmailAndInventoryId(email,
            inventoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_INVENTORY_USER));

    if (!hasRole(inventoryUser)) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }

    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

    int oldQuantity = product.getQuantity();
    product.setQuantity(newQuantity);
    productRepository.save(product);
    productSearchRepository.save(product);  // ES 인덱싱

    ProductHistory history = ProductHistory.builder()
        .product(product)
        .oldQuantity(oldQuantity)
        .newQuantity(newQuantity)
        .changedByEmail(email)
        .changeReason(reason)
        .build();
    productHistoryRepository.save(history);

    return ProductResponse.of(product);
  }

  // ES 검색 관련 메서드
  public List<ProductResponse> searchProductsByName(String name) {
    List<Product> products = productSearchRepository.findByName(name);
    return products.stream()
        .map(ProductResponse::of)
        .collect(Collectors.toList());
  }

  public List<ProductResponse> searchProductsByDescription(String description) {
    List<Product> products = productSearchRepository.findByDescriptionContaining(description);
    return products.stream()
        .map(ProductResponse::of)
        .collect(Collectors.toList());
  }

  public List<ProductResponse> searchProductsByPriceRange(int minPrice, int maxPrice) {
    List<Product> products = productSearchRepository.findByPriceBetween(minPrice, maxPrice);
    return products.stream()
        .map(ProductResponse::of)
        .collect(Collectors.toList());
  }

  public List<ProductResponse> searchProductsByQuantityRange(int minQuantity, int maxQuantity) {
    List<Product> products = productSearchRepository.findByQuantityBetween(minQuantity,
        maxQuantity);
    return products.stream()
        .map(ProductResponse::of)
        .collect(Collectors.toList());
  }

  public String exportExcel(Long productId, String directoryPath, LocalDate startDate,
      LocalDate endDate) throws IOException {
    String user = getCurrentUserEmail();

    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

    Category category = product.getCategory();
    Inventory inventory = category.getInventory();
    String inventoryName = inventory.getName();

    // 파일 경로 및 제목 설정
    String filePath = directoryPath + "/product_";
    String title = inventoryName + " 재고 " + startDate + " - " + endDate+ ".xlsx";

    // 엑셀 내보내기 로그 기록
    ExcelLog exportLog = ExcelLog.builder()
        .user(user)
        .exportTime(LocalDateTime.now())
        .startDate(startDate)
        .endDate(endDate)
        .filePath(filePath)
        .build();
    excelLogRepository.save(exportLog);

    // 엑셀 파일 생성
    XSSFWorkbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("재고 현황");

    // 제목 행 추가
    Row titleRow = sheet.createRow(0);
    Cell titleCell = titleRow.createCell(0);
    titleCell.setCellValue(title);

    // 헤더 행 추가 (첫 번째 열에 제품명, 이후 날짜별로 열 추가)
    Row headerRow = sheet.createRow(1);
    headerRow.createCell(0).setCellValue("제품명");

    // 날짜 헤더 추가 (startDate ~ endDate 범위 내의 날짜)
    int dateIndex = 1;
    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      headerRow.createCell(dateIndex++).setCellValue(date.toString());
    }

    // 제품 이름 추가 (첫 번째 열)
    Row productRow = sheet.createRow(2);
    productRow.createCell(0).setCellValue(product.getName());

    // 제품 이력 데이터 가져오기
    List<ProductHistory> histories = productHistoryRepository.findByProductIdAndChangeDateBetween(
        productId, startDate, endDate);

    // 날짜별로 변동 수량 기록
    for (ProductHistory history : histories) {
      LocalDate changeDate = history.getChangeDate().toLocalDate();
      int columnIndex = (int) startDate.until(changeDate).getDays() + 1;  // 날짜에 맞는 열 인덱스
      productRow.createCell(columnIndex)
          .setCellValue(history.getNewQuantity() - history.getOldQuantity());
    }

    // 엑셀 파일 출력
    try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
      workbook.write(fileOut);
    }

    // 엑셀 작업 마무리
    workbook.close();

    // 파일 경로 반환
    return filePath;
  }


  // 권한 확인
  private boolean hasRole(InventoryUser inventoryUser) {
    return inventoryUser.getRole() == Role.ADMIN || inventoryUser.getRole() == Role.MANAGER;
  }

  // 인벤토리 유저 확인
  private boolean isInventoryUser(InventoryUser inventoryUser) {
    return inventoryUser != null;
  }

  private String getCurrentUserEmail() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
}
