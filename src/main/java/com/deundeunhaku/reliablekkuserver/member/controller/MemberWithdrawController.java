package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/my-pages")
public class MemberWithdrawController {
    private final MemberService memberService;

    @PatchMapping("/withdraw")
    public ResponseEntity<String> withdraw(HttpServletResponse response, @AuthenticationPrincipal Member member, @CookieValue(name = "refreshToken", required = false) Cookie refreshTokenCookie, @CookieValue(name = "accessToken", required = false) Cookie accessTokenCookie) {

        boolean isMemberWithdraw = memberService.checkMemberIsWithdraw(member);

        if (isMemberWithdraw) {
            return ResponseEntity.badRequest().body("이미 탈퇴한 회원이거나 회원을 찾을 수 없습니다.");
        }
        memberService.setMemberWithdraw(member);
        setTokenExpired(response, refreshTokenCookie, accessTokenCookie);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    private static void setTokenExpired(HttpServletResponse response, Cookie refreshTokenCookie, Cookie accessTokenCookie) {
        refreshTokenCookie.setMaxAge(0);
        accessTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);
        response.addCookie(accessTokenCookie);
    }
}