package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.cookies.CookieDocumentation;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberLogoutControllerTest extends BaseControllerTest {
    @InjectMocks
    private MemberLogoutController logoutController;

    @MockBean
    private HttpServletRequest request;

    @MockBean
    private HttpServletResponse response;

    @Test
    void 로그아웃시_refreshToken과_accessToken을_삭제한다() throws Exception {
        //given
        String logoutUrl = API + "/my-pages/logout";

        Cookie refreshTokenCookie = new Cookie("refreshToken", "example_refresh_token");
        Cookie accessTokenCookie = new Cookie("accessToken", "example_access_token");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie, accessTokenCookie});

        ResponseEntity<Void> responseEntity = logoutController.logout(response, refreshTokenCookie, accessTokenCookie);

        verify(response).addCookie(refreshTokenCookie);
        verify(response).addCookie(accessTokenCookie);
        assert (responseEntity.getStatusCodeValue() == 200);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(logoutUrl)
                        .cookie(refreshTokenCookie)
                        .cookie(accessTokenCookie)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk())
                .andDo(document("my-pages/logout/success",
                        CookieDocumentation.requestCookies(
                                CookieDocumentation.cookieWithName("refreshToken").description("refreshToken"),
                                CookieDocumentation.cookieWithName("accessToken").description("accessToken")
                        )
                ));
    }

    @Test
    void refreshToken과_accessToken이_없는상태에서_로그아웃시_null값을_반환한다(){
        when(request.getCookies()).thenReturn(null);
        ResponseEntity<Void> responseEntity = logoutController.logout(response, null, null);

        assert(responseEntity.getStatusCodeValue() == 200);
    }

}