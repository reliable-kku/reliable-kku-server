package com.deundeunhaku.reliablekkuserver.fcm.controller;

import com.deundeunhaku.reliablekkuserver.fcm.dto.FcmTokenRequest;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/fcm")
public class FcmController {

  private final MemberService memberService;

  @PostMapping
  public ResponseEntity<Void> updateFcmToken(@AuthenticationPrincipal Member member,
      @RequestBody FcmTokenRequest request) {
    memberService.updateFcmToken(member.getId(), request.token());

    return ResponseEntity.ok().build();
  }

}
