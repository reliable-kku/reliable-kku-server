package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cookies.CookieDocumentation;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class MemberWithdrawControllerTest extends BaseControllerTest {

    @MockBean
    MemberService memberService;
    @Test
    void 회원을_탈퇴한다() throws Exception {
        //given
        String withdrawUrl = API + "/my-pages/withdraw";

        Cookie refreshTokenCookie = new Cookie("refreshToken", "example_refresh_token");
        Cookie accessTokenCookie = new Cookie("accessToken", "example_access_token");
        when(memberService.checkMemberIsWithdraw(any()))
                .thenReturn(false);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch(withdrawUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(refreshTokenCookie)
                        .cookie(accessTokenCookie))
                .andDo(print());
        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string("회원 탈퇴가 완료되었습니다."))
                .andDo(document("my-pages/member-withdraw/success",
                                CookieDocumentation.requestCookies(
                                        CookieDocumentation.cookieWithName("refreshToken").description("refreshToken"),
                                        CookieDocumentation.cookieWithName("accessToken").description("accessToken")
                                )
                        )
                );
    }

    @Test
    void 이미_삭제된_회원이면_에러를_반환한다() throws Exception {
        //given
        String withdrawUrl = API + "/my-pages/withdraw";

        Cookie refreshTokenCookie = new Cookie("refreshToken", "example_refresh_token");
        Cookie accessTokenCookie = new Cookie("accessToken", "example_access_token");
        when(memberService.checkMemberIsWithdraw(any()))
                .thenReturn(true);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch(withdrawUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(refreshTokenCookie)
                        .cookie(accessTokenCookie))
                .andDo(print());
        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().string("이미 탈퇴한 회원이거나 회원을 찾을 수 없습니다."))
                .andDo(document("my-pages/member-withdraw/fail",
                                CookieDocumentation.requestCookies(
                                        CookieDocumentation.cookieWithName("refreshToken").description("refreshToken"),
                                        CookieDocumentation.cookieWithName("accessToken").description("accessToken")
                                )
                        )
                );
    }
}