package com.deundeunhaku.reliablekkuserver.member.service;
import com.deundeunhaku.reliablekkuserver.BaseServiceTest;
import com.deundeunhaku.reliablekkuserver.common.exception.LoginFailedException;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.LoginRequest;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberMyPageResponse;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberPasswordChangeRequest;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class MemberServiceTest extends BaseServiceTest {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthenticationManager authenticationManager;

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
    memberService.login(request.phoneNumber(), request.password());

    //then
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

  @Test
  void 현재_비밀번호와_입력한_현재_비밀번호가_동일하면_true를_리턴한다(){
    //given
    Member member = Member.builder().password("mockedPassword").build();
    String password = "mockedPassword";

    when(passwordEncoder.matches(member.getPassword(), password)).thenReturn(true);
    //when
    boolean result = memberService.isMemberPasswordMatch(member, password);
    //then
    assertThat(result).isTrue();
  }

  @Test
  void 현재_비밀번호와_입력한_현재_비밀번호가_다르면_false를_리턴한다(){
    //given
    Member member = Member.builder().password("mockedPassword").build();
    String password = "mockedPassword1234";

    when(passwordEncoder.matches(member.getPassword(), password)).thenReturn(false);
    //when
    boolean result = memberService.isMemberPasswordMatch(member, password);
    //then
    assertThat(result).isFalse();
  }

  @Test
  void 변경할_비밀번호를_받으면_비밀번호를_변경한다() {
    //given
    MemberPasswordChangeRequest request = MemberPasswordChangeRequest.of( "changePassword");

    Member member = Member.builder().password("password").build();
    //when
    boolean isChanged = memberService.changeMemberPassword(member, request);
    //then
    assertThat(isChanged).isTrue();
  }

  @Test
  void 멤버의_탈퇴여부를_확인하고_false면_isWithdraw를_true로_반환한다(){
    //given
    Member member = Member.builder().isWithdraw(false).build();
    //when
    boolean isWithdraw = memberService.checkMemberIsWithdraw(member);
    memberService.setMemberWithdraw(member);
    //then
    assertThat(isWithdraw).isFalse();
    assertThat(member.isWithdraw()).isTrue();
  }

  @Test
  void 마이페이지의_정보를_가져오는지_검증한다(){
    //given
    Member member = Member.builder().realName("kim").level(5).build();
    //when
    MemberMyPageResponse response = memberService.getMyPageInfo(member);
    //then
    assertThat(response.realName()).isEqualTo(member.getRealName());
    assertThat(response.level()).isEqualTo(member.getLevel());
  }
}