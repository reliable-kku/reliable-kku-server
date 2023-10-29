package com.deundeunhaku.reliablekkuserver.member.service;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.AdminMemberManagementResponse;
import com.deundeunhaku.reliablekkuserver.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminMemberService {

  private final PasswordEncoder passwordEncoder;
  private final MemberRepository memberRepository;

  public Member login(String phoneNumber, String password) {
    return memberRepository.findByPhoneNumber(phoneNumber)
        .filter(member -> passwordEncoder.matches(password, member.getPassword()))
        .filter(Member::isAdmin)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
  }

  public Slice<AdminMemberManagementResponse> getMemberList(String searchKeyword, Pageable pageable) {
    return memberRepository.findMemberBySearchKeyword(searchKeyword, pageable);


  }
}
