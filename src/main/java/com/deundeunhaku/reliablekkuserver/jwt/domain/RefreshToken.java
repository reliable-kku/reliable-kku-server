package com.deundeunhaku.reliablekkuserver.jwt.domain;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {

  @Id
  @GeneratedValue
  private Long id;

  @NotNull
  @OneToOne(fetch = FetchType.LAZY)
  private Member member;

  @NotNull
  private String refreshToken;

  @Builder
  public RefreshToken(Member member, String refreshToken) {
    this.member = member;
    this.refreshToken = refreshToken;
  }

  public void updateRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
