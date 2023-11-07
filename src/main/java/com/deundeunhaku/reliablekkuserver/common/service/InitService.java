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

    memberRepository.save(
        Member.builder()
            .phoneNumber("01076152022")
            .password(passwordEncoder.encode("1234"))
            .realName("선규땅")
            .role(Role.USER)
            .build()
    );

    memberRepository.save(
        Member.builder()
            .phoneNumber("01057490339")
            .password(passwordEncoder.encode("1234"))
            .realName("갱쥬")
            .role(Role.USER)
            .build()
    );

    memberRepository.save(
        Member.builder()
            .phoneNumber("01087665450")
            .password(passwordEncoder.encode("1234"))
            .realName("나는과대다")
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
            .menuImageUrl("https://deundeunhaku-bucket.s3.ap-northeast-2.amazonaws.com/redbean1.png")
            .build()
    );
    menuRepository.save(
        Menu.builder()
            .name("슈크림붕")
            .description("슈붕이다 냠냠")
            .pricePerOne(700)
            .pricePerThree(2000)
            .isSale(true)
            .menuImageUrl("https://deundeunhaku-bucket.s3.ap-northeast-2.amazonaws.com/cream1.png")
            .build()
    );
    menuRepository.save(
        Menu.builder()
            .name("누텔라붕")
            .description("누붕누붕 냠냠")
            .pricePerOne(1000)
            .pricePerThree(3000)
            .isSale(true)
            .menuImageUrl("https://deundeunhaku-bucket.s3.ap-northeast-2.amazonaws.com/chocolate1.png")
            .build()
    );

  }

}
