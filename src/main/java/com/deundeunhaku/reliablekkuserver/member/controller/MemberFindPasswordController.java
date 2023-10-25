package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.common.dto.BaseMessageResponse;
import com.deundeunhaku.reliablekkuserver.member.dto.FindPasswordRequest;
import com.deundeunhaku.reliablekkuserver.member.dto.PhoneNumberRequest;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/find-password")
public class MemberFindPasswordController {

  private final MemberService memberService;

  @PostMapping("/phone-number/certification-number")
  public ResponseEntity<Void> sendCertificationNumber(@RequestBody PhoneNumberRequest request) {
    memberService.sendCertificationNumber(request.phoneNumber(), LocalDate.now());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/phone-number/certification-number")
  public ResponseEntity<BaseMessageResponse> checkCertificationNumber(@RequestParam String phoneNumber, @RequestParam Integer certificationNumber) {

    boolean isCertificationNumberCorrect = memberService.isCertificationNumberCorrect(phoneNumber, certificationNumber);

    if (isCertificationNumberCorrect) {
      return ResponseEntity.ok(BaseMessageResponse.of("인증되었습니다."));
    } else {
      return ResponseEntity.badRequest().body(BaseMessageResponse.of("인증번호가 일치하지 않습니다."));
    }
  }

  @PostMapping
  public ResponseEntity<Void> changePassword(@RequestBody FindPasswordRequest request) {
    memberService.changePasswordWithRandomNumber(request.phoneNumber(), request.certificationNumber());
    return ResponseEntity.ok().build();
  }

}
