package com.deundeunhaku.reliablekkuserver.common.service;

import com.deundeunhaku.reliablekkuserver.member.constant.Role;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InitService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @PostConstruct
  public void init() {
    memberRepository.save(
        Member.builder()
            .phoneNumber("admin")
            .password(passwordEncoder.encode("1234"))
            .realName("관리자")
            .role(Role.ADMIN)
            .build()
    );

    memberRepository.save(
        Member.builder()
            .phoneNumber("01012341234")
            .password(passwordEncoder.encode("1234"))
            .realName("테스트")
            .role(Role.USER)
            .build()
    );
  }

}
