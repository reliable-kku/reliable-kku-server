package com.deundeunhaku.reliablekkuserver.fcm;

import com.deundeunhaku.reliablekkuserver.fcm.domain.FcmToken;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

  Optional<FcmToken> findByTokenAndMember(String token, Member member);

  List<FcmToken> findByMember(Member member);

}
