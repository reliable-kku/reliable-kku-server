package com.deundeunhaku.reliablekkuserver.member.domain;

import com.deundeunhaku.reliablekkuserver.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class CertificationNumber extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String phoneNumber;

  private Integer certificationNumber;

  @ColumnDefault("false")
  private Boolean isCertified;

  public void certify() {
    this.isCertified = true;
  }

  @Builder
  public CertificationNumber(Long id, String phoneNumber, Integer certificationNumber,
      Boolean isCertified) {
    this.id = id;
    this.phoneNumber = phoneNumber;
    this.certificationNumber = certificationNumber;
    this.isCertified = isCertified;
  }
}
