package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cookies.CookieDocumentation;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class MemberLogoutControllerTest extends BaseControllerTest {

    @Test
    void 로그아웃시_refreshToken과_accessToken을_삭제한다() throws Exception {
        //given
        String logoutUrl = API + "/my-pages/logout";

        Cookie refreshTokenCookie = new Cookie("refreshToken", "example_refresh_token");
        Cookie accessTokenCookie = new Cookie("accessToken", "example_access_token");

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(logoutUrl)
                        .cookie(refreshTokenCookie)
                        .cookie(accessTokenCookie)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

        // Then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("my-pages/logout/success",
                        CookieDocumentation.requestCookies(
                                CookieDocumentation.cookieWithName("refreshToken").description("refreshToken"),
                                CookieDocumentation.cookieWithName("accessToken").description("accessToken")
                        )
                ));
    }

        @Test
        void refreshToken과_accessToken이_없는상태에서_로그아웃시_에러를_반환한다() throws Exception {
        // Given
        String logoutUrl = API + "/my-pages/logout";
        // When
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(logoutUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());
        // Then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}