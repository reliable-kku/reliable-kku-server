package com.deundeunhaku.reliablekkuserver.jwt.service;

import com.deundeunhaku.reliablekkuserver.jwt.domain.RefreshToken;
import com.deundeunhaku.reliablekkuserver.jwt.repository.RefreshTokenRepository;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public RefreshToken save(Member member, RefreshToken refreshToken) {
    if (refreshTokenRepository.existsByMember(member)) {
      RefreshToken findRefreshToken = refreshTokenRepository.findByMember(member)
          .orElseThrow(() -> new IllegalArgumentException("member에 해당하는 refreshToken이 없습니다."));
      findRefreshToken.updateRefreshToken(refreshToken.getRefreshToken());

      return findRefreshToken;
    }

    return refreshTokenRepository.save(refreshToken);
  }
}
