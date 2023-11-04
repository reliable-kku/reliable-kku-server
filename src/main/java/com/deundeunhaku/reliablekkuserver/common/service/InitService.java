package com.deundeunhaku.reliablekkuserver.common.service;

import com.deundeunhaku.reliablekkuserver.member.constant.Role;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import com.deundeunhaku.reliablekkuserver.menu.repository.MenuRepository;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//@Profile("local")
@RequiredArgsConstructor
@Service
public class InitService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final MenuRepository menuRepository;

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

    menuRepository.save(
        Menu.builder()
            .name("팥붕")
            .description("팥붕이다 냠냠")
            .pricePerOne(700)
            .pricePerThree(2000)
            .isSale(true)
            .menuImageUrl("https://deundeunhaku-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%91%E1%85%A1%E1%87%80%E1%84%87%E1%85%AE%E1%86%BC.png")
            .build()
    );
    menuRepository.save(
        Menu.builder()
            .name("슈크림붕")
            .description("슈붕이다 냠냠")
            .pricePerOne(700)
            .pricePerThree(2000)
            .isSale(true)
            .menuImageUrl("https://deundeunhaku-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%89%E1%85%B2%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%B7%E1%84%87%E1%85%AE%E1%86%BC.png")
            .build()
    );
    menuRepository.save(
        Menu.builder()
            .name("누텔라붕")
            .description("누붕누붕 냠냠")
            .pricePerOne(1000)
            .pricePerThree(3000)
            .isSale(true)
            .menuImageUrl("https://deundeunhaku-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%82%E1%85%AE%E1%84%90%E1%85%A6%E1%86%AF%E1%84%85%E1%85%A1%E1%84%87%E1%85%AE%E1%86%BC.png")
            .build()
    );

  }

}
