package com.deundeunhaku.reliablekkuserver.member.service;

import com.deundeunhaku.reliablekkuserver.common.exception.LoginFailedException;
import com.deundeunhaku.reliablekkuserver.member.constant.Role;
import com.deundeunhaku.reliablekkuserver.member.domain.CertificationNumber;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberMyPageResponse;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberPasswordChangeRequest;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberRegisterRequest;
import com.deundeunhaku.reliablekkuserver.member.repository.CertificationNumberRepository;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import com.deundeunhaku.reliablekkuserver.sms.dto.SmsCertificationNumber;
import com.deundeunhaku.reliablekkuserver.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final CertificationNumberRepository certificationNumberRepository;
  private final SmsService smsService;
  private final AuthenticationManager authenticationManager;

      public Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

  public void login(String phoneNumber, String password) {

    Member findMember = memberRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(LoginFailedException::new);

    boolean isPasswordMatch = passwordEncoder.matches(password, findMember.getPassword());

    if (!isPasswordMatch) {
      throw new LoginFailedException();
    }else {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(phoneNumber, password));

    }
  }

  public boolean isPhoneNumberDuplicate(String phoneNumber) {
    Optional<Member> phoneNumberDuplicate = memberRepository.findByPhoneNumber(phoneNumber);

    return phoneNumberDuplicate.isPresent();
  }

  @Transactional
  public void sendCertificationNumber(String phoneNumber, LocalDate today) {
    List<CertificationNumber> todayCertificationNumberList = certificationNumberRepository.findAllByCreatedAt(
        today);

    if (todayCertificationNumberList.size() >= 10) {
      throw new IllegalArgumentException("하루에 10번 이상 인증번호를 요청할 수 없습니다.");
    }

    SmsCertificationNumber smsResponse = smsService.sendCertificationNumberToPhoneNumber(
        phoneNumber);

    certificationNumberRepository.save(
        CertificationNumber.builder()
            .certificationNumber(smsResponse.certificationNumber())
            .phoneNumber(phoneNumber)
            .build()
    );

  }

  public boolean isCertificationNumberCorrect(String phoneNumber, Integer certificationNumber) {

    Optional<CertificationNumber> findCertificationNumber = certificationNumberRepository.findTop1ByPhoneNumberOrderByCreatedAtDesc(
        phoneNumber);

    if (findCertificationNumber.isPresent() && findCertificationNumber.get()
        .getCertificationNumber().equals(certificationNumber)) {
      findCertificationNumber.get().certify();
      return true;
    }else{
      return false;
    }
  }

  @Transactional
  public void register(MemberRegisterRequest request) {

    CertificationNumber certificationNumber = certificationNumberRepository.findTop1ByPhoneNumberOrderByCreatedAtDesc(
            request.phoneNumber())
        .orElseThrow(() -> new IllegalArgumentException("인증번호가 일치하지 않습니다."));

    if (certificationNumber.getIsCertified().equals(false)) {
      throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    memberRepository.save(
        Member.builder()
            .realName(request.realName())
            .phoneNumber(request.phoneNumber())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.USER)
            .build()
    );

  }

  @Transactional
  public void changePasswordWithRandomNumber(String phoneNumber, int certificationNumber) {

    CertificationNumber findCertificationNumber = certificationNumberRepository.findTop1ByPhoneNumberOrderByCreatedAtDesc(
            phoneNumber)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    if (findCertificationNumber.getIsCertified().equals(false) || findCertificationNumber.getCertificationNumber().equals(certificationNumber)) {
      throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    Member findMember = memberRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    String newPassword = UUID.randomUUID().toString().substring(7);
    findMember.changePassword(passwordEncoder.encode(newPassword));

    smsService.sendNewPasswordToPhoneNumber(phoneNumber, newPassword);

  }

  public boolean isMemberPasswordMatch(Member member, String password) {

    if (passwordEncoder.matches(member.getPassword(), password)) {
      return true;
    }else {
      return false;
    }
  }

  @Transactional
  public boolean changeMemberPassword(Member member, MemberPasswordChangeRequest request) {
    member.changePassword(passwordEncoder.encode(request.password()));
    return true;
  }

  public boolean checkMemberIsWithdraw(Member member) {
      return member.isWithdraw();
  }

  public void setMemberWithdraw(Member member) {
    member.withdraw();
  }

  public MemberMyPageResponse getMyPageInfo(Member member) {
        return member.toMemberMyPageResponse();
  }
}
