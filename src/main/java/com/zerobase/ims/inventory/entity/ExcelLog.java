package com.zerobase.ims.inventory.entity;

import com.zerobase.ims.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String user;

  @Column(nullable = false)
  private LocalDateTime exportTime; // 엑셀 생성 시간

  @Column(nullable = false)
  private LocalDate startDate; // 엑셀에 포함된 시작 날짜

  @Column(nullable = false)
  private LocalDate endDate; // 엑셀에 포함된 종료 날짜

  @Column(nullable = false)
  private String filePath; // 엑셀 파일 경로
}
