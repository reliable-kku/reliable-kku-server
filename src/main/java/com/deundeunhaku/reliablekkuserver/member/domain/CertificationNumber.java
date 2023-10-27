package com.deundeunhaku.reliablekkuserver.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;

@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CertificationNumber {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String phoneNumber;

  private Integer certificationNumber;

  @ColumnDefault("false")
  private Boolean isCertified;

  @CreatedDate
  private LocalDate createdAt;

  public void certify() {
    this.isCertified = true;
  }

  @Builder
  public CertificationNumber(Long id, String phoneNumber, Integer certificationNumber,
      Boolean isCertified, LocalDate createdAt) {
    this.id = id;
    this.phoneNumber = phoneNumber;
    this.certificationNumber = certificationNumber;
    this.isCertified = isCertified;
    this.createdAt = createdAt;
  }
}
