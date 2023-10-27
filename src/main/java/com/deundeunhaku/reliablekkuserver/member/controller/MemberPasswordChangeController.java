package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberPasswordChangeRequest;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberPasswordMatchResponse;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/my-pages")
public class MemberPasswordChangeController {
    private final MemberService memberService;

    @PostMapping("/change-password/verify-current-password")
    public ResponseEntity<MemberPasswordMatchResponse> getCurrentPassword(@AuthenticationPrincipal Member member, @RequestBody final String password){

        boolean isPasswordMatch = memberService.isMemberPasswordMatch(member, password);
        return ResponseEntity.ok(MemberPasswordMatchResponse.of(isPasswordMatch));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal Member member, @RequestBody MemberPasswordChangeRequest request) {
        boolean isChanged = memberService.changeMemberPassword(member, request);
        return ResponseEntity.ok().build();
    }


}