package com.deundeunhaku.reliablekkuserver.store.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "STORE")
@Entity
public class Store {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Setter
  @ColumnDefault("false")
  private Boolean isOpened;

  public Store(Boolean isOpened) {
    this.isOpened = isOpened;
  }

  @Builder
  public Store(Long id, Boolean isOpened) {
    this.id = id;
    this.isOpened = isOpened;
  }
}
