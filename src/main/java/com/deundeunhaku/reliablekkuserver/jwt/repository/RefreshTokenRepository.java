package com.deundeunhaku.reliablekkuserver.jwt.repository;

import com.deundeunhaku.reliablekkuserver.jwt.domain.RefreshToken;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    boolean existsByMember(Member member);

    Optional<RefreshToken> findByMember(Member member);
}
