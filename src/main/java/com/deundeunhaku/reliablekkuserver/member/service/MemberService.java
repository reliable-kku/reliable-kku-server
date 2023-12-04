package com.deundeunhaku.reliablekkuserver.member.service;

import com.deundeunhaku.reliablekkuserver.common.exception.LoginFailedException;
import com.deundeunhaku.reliablekkuserver.fcm.FcmTokenRepository;
import com.deundeunhaku.reliablekkuserver.fcm.domain.FcmToken;
import com.deundeunhaku.reliablekkuserver.jwt.domain.RefreshToken;
import com.deundeunhaku.reliablekkuserver.jwt.repository.RefreshTokenRepository;
import com.deundeunhaku.reliablekkuserver.member.constant.Role;
import com.deundeunhaku.reliablekkuserver.member.domain.CertificationNumber;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberMyPageResponse;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberPasswordChangeRequest;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberRegisterRequest;
import com.deundeunhaku.reliablekkuserver.member.repository.CertificationNumberRepository;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import com.deundeunhaku.reliablekkuserver.sms.dto.SmsCertificationNumber;
import com.deundeunhaku.reliablekkuserver.sms.service.CoolSmsService;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

  // private final BCryptPasswordEncoder
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final CertificationNumberRepository certificationNumberRepository;
  private final CoolSmsService smsService;
  private final AuthenticationManager authenticationManager;

  private final RefreshTokenRepository refreshTokenRepository;
  private final FcmTokenRepository fcmTokenRepository;
  private final EntityManager entityManager;

  public Member findMemberById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
  }

  public void login(String phoneNumber, String password) {

    Member findMember = memberRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(LoginFailedException::new);

    if (findMember.isWithdraw()) {
      throw new IllegalArgumentException("이미 회원 탈퇴한 유저입니다.");
    }

    boolean isPasswordMatch = passwordEncoder.matches(password, findMember.getPassword());

    if (!isPasswordMatch) {
      throw new LoginFailedException();
    } else {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(phoneNumber, password));

    }
  }

  public boolean isPhoneNumberDuplicate(String phoneNumber) {
    Optional<Member> phoneNumberDuplicate = memberRepository.findByPhoneNumber(phoneNumber);

    return phoneNumberDuplicate.isPresent();
  }

  @Transactional
  public void sendCertificationNumber(String phoneNumber, LocalDateTime today) {
    List<CertificationNumber> todayCertificationNumberList = certificationNumberRepository.findAllByPhoneNumberAndCreatedAt(
        phoneNumber, today);

    if (todayCertificationNumberList.size() >= 10) {
      throw new IllegalArgumentException("하루에 10번 이상 인증번호를 요청할 수 없습니다.");
    }

    SmsCertificationNumber smsResponse = smsService.sendCertificationNumberToPhoneNumber(
        phoneNumber);
    log.info("인증번호 : {}", smsResponse.certificationNumber());
    certificationNumberRepository.save(
        CertificationNumber.builder()
            .certificationNumber(smsResponse.certificationNumber())
            .phoneNumber(phoneNumber)
            .build()
    );

  }

  @Transactional
  public boolean isCertificationNumberCorrect(String phoneNumber, Integer certificationNumber) {

    Optional<CertificationNumber> findCertificationNumber = certificationNumberRepository.findFirstByPhoneNumberOrderByCreatedAtDesc(
        phoneNumber);

    if (findCertificationNumber.isPresent()) {
      if (findCertificationNumber.get().getCertificationNumber().equals(certificationNumber)) {
        log.info("인증번호 일치 : {} {}", phoneNumber, certificationNumber);
        findCertificationNumber.get().certify();
        return true;
      }
      return false;
    }

    return false;
  }

  @Transactional
  public void register(MemberRegisterRequest request) {

    CertificationNumber certificationNumber = certificationNumberRepository.findFirstByPhoneNumberOrderByCreatedAtDesc(
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

    CertificationNumber findCertificationNumber = certificationNumberRepository.findFirstByPhoneNumberOrderByCreatedAtDesc(
            phoneNumber)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    if (findCertificationNumber.getIsCertified().equals(false)
        || findCertificationNumber.getCertificationNumber() != (certificationNumber)) {
      throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    Member findMember = memberRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    String newPassword = UUID.randomUUID().toString().substring(0, 6);
    findMember.changePassword(passwordEncoder.encode(newPassword));

    smsService.sendNewPasswordToPhoneNumber(phoneNumber, newPassword);

  }

  @Transactional
  public boolean isMemberPasswordMatch(String password, Member member) {

    return passwordEncoder.matches(password, member.getPassword());
  }

  @Transactional
  public boolean changeMemberPassword(Member member, MemberPasswordChangeRequest request) {
    entityManager.clear();

    Member findMember = memberRepository.findById(member.getId())
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    String encodedPassword = passwordEncoder.encode(request.password());
    findMember.changePassword(encodedPassword);
    return true;
  }


  public boolean checkMemberIsWithdraw(Member member) {
    return member.isWithdraw();
  }

  @Transactional
  public void setMemberWithdraw(Member member) {
    entityManager.clear();

    Member findMember = memberRepository.findById(member.getId())
        .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

    findMember.withdraw();
  }

  public MemberMyPageResponse getMyPageInfo(Member member) {
    return member.toMemberMyPageResponse();
  }

  @Transactional
  public void updateFcmToken(Member findMember, String token) {
    entityManager.clear();

    Member member = memberRepository.findById(findMember.getId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

    Optional<FcmToken> optionalFcmToken = fcmTokenRepository.findByTokenAndMember(token, member);

    if (optionalFcmToken.isEmpty()) {
        fcmTokenRepository.save(
            FcmToken.builder()
                .token(token)
                .member(member)
                .build()
        );
    }
//    member.setFirebaseToken(token);
  }

  @Transactional
  public void saveRefreshToken(String phoneNumber, String refreshToken) {

    Member member = memberRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

    Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMember(member);

    if (optionalRefreshToken.isPresent()) {
      optionalRefreshToken.get().setRefreshToken(refreshToken);
    } else {
      RefreshToken newRefreshToken = RefreshToken.builder()
          .member(member)
          .refreshToken(refreshToken)
          .build();
      refreshTokenRepository.save(newRefreshToken);
    }
  }
}
