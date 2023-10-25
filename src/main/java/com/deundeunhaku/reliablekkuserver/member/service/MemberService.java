package com.deundeunhaku.reliablekkuserver.member.service;

import com.deundeunhaku.reliablekkuserver.common.exception.LoginFailedException;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member login(String phoneNumber, String password) {

    Member findMember = memberRepository.findByPhoneNumber(phoneNumber)
        .orElseThrow(LoginFailedException::new);

    boolean isPasswordMatch = passwordEncoder.matches(password, findMember.getPassword());

    if (isPasswordMatch) {
      return findMember;
    } else {
      throw new LoginFailedException();
    }
  }
}
