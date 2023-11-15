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

@Profile({"prod"})
@RequiredArgsConstructor
@Service
public class ProductionInitService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final MenuRepository menuRepository;

  @PostConstruct
  public void init() {
    memberRepository.save(
//        FIXME : 관리자 비밀번호, 아이디 변경 필요
        Member.builder()
            .phoneNumber("admin")
            .password(passwordEncoder.encode("1234"))
            .realName("관리자")
            .role(Role.ADMIN)
            .build()
    );

    menuRepository.save(
        Menu.builder()
            .name("팥 붕어빵")
            .description("머리부터 꼬리까지 꽉 찬 ‘.. 진짜 팥’")
            .pricePerOne(700)
            .pricePerThree(2000)
            .isSale(true)
            .menuImageUrl("https://deundeunhaku-bucket.s3.ap-northeast-2.amazonaws.com/redbean1.png")
            .build()
    );
    menuRepository.save(
        Menu.builder()
            .name("슈크림 붕어빵")
            .description("파트라슈는 있어도 파트라팥은 없다")
            .pricePerOne(700)
            .pricePerThree(2000)
            .isSale(true)
            .menuImageUrl("https://deundeunhaku-bucket.s3.ap-northeast-2.amazonaws.com/cream1.png")
            .build()
    );
    menuRepository.save(
        Menu.builder()
            .name("누텔라 붕어빵")
            .description("악마의 잼에 잡아먹혀 흑화된 붕어빵")
            .pricePerOne(1000)
            .pricePerThree(3000)
            .isSale(true)
            .menuImageUrl("https://deundeunhaku-bucket.s3.ap-northeast-2.amazonaws.com/chocolate1.png")
            .build()
    );

  }

}
