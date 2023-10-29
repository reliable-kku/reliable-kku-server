package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberMyPageResponse;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/my-pages")
public class MemberMyPageController {

    private final MemberService memberService;
    @GetMapping("/member")
    public ResponseEntity<MemberMyPageResponse> getMemberInfo(@AuthenticationPrincipal Member member){
        MemberMyPageResponse response = memberService.getMyPageInfo(member);
        return ResponseEntity.ok(response);
    }
}
