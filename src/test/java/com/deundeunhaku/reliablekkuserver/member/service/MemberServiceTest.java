package com.deundeunhaku.reliablekkuserver.member.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.deundeunhaku.reliablekkuserver.BaseServiceTest;
import com.deundeunhaku.reliablekkuserver.common.exception.LoginFailedException;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.LoginRequest;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

class MemberServiceTest extends BaseServiceTest {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Test
  void 회원이_존재하는지_검증한다() throws Exception {
    //given
    Long memberId = 1L;
    String phoneNumber = "01012341234";
    String password = "password";
    LoginRequest request = LoginRequest.of(
        phoneNumber,
        password
    );

    Member member = Member.builder()
        .id(memberId)
        .realName("realName")
        .phoneNumber(phoneNumber)
        .password(password)
        .build();

    when(memberRepository.findByPhoneNumber(phoneNumber))
        .thenReturn(Optional.of(member));

    when(passwordEncoder.matches(request.password(), password))
        .thenReturn(true);

    //when
    Member findMember = memberService.login(request.phoneNumber(), request.password());

    //then
    Assertions.assertThat(findMember).isEqualTo(member);

  }

  @Test
  void 회원이_존재하지않으면_에러를던진다() throws Exception {
    //given
    Long memberId = 1L;
    String phoneNumber = "01012341234";
    String password = "password";
    LoginRequest request = LoginRequest.of(
        phoneNumber,
        password
    );

    Member member = Member.builder()
        .id(memberId)
        .realName("realName")
        .phoneNumber(phoneNumber)
        .password(password)
        .build();

    when(memberRepository.findByPhoneNumber(phoneNumber))
        .thenReturn(Optional.empty());

    //when
    //then
    assertThrows(LoginFailedException.class,
        () -> memberService.login(request.phoneNumber(), request.password()));
  }

  @Test
  void 비밀번호가_일치하지않을때_에러를_던진다() throws Exception {
    //given
    Long memberId = 1L;
    String phoneNumber = "01012341234";
    String password = "password";
    LoginRequest request = LoginRequest.of(
        phoneNumber,
        password
    );

    Member member = Member.builder()
        .id(memberId)
        .realName("realName")
        .phoneNumber(phoneNumber)
        .password(password)
        .build();

    when(memberRepository.findByPhoneNumber(phoneNumber))
        .thenReturn(Optional.of(member));

    when(passwordEncoder.matches(request.password(), password))
        .thenReturn(false);

    //when
    //then
    assertThrows(LoginFailedException.class,
        () -> memberService.login(request.phoneNumber(), request.password()));
  }


}