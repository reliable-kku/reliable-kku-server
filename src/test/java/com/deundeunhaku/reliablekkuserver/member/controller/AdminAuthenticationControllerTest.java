package com.deundeunhaku.reliablekkuserver.member.controller;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.jwt.constants.TokenDuration;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.AdminLoginRequest;
import com.deundeunhaku.reliablekkuserver.member.service.AdminMemberService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class AdminAuthenticationControllerTest extends BaseControllerTest {

  @MockBean
  private AdminMemberService adminMemberService;

  @Test
  void 로그인을_하면_jwt_토큰을_cookie에_담아_반환한다() throws Exception {
    //given
    Long memberId = 1L;
    String username = "admin";
    String password = "password";
    AdminLoginRequest request = AdminLoginRequest.of(
        username,
        password
    );

    Member member = Member.builder()
        .id(1L)
        .realName("realName")
        .phoneNumber(username)
        .password(password)
        .build();

    when(jwtTokenUtils.generateJwtToken(username,
        TokenDuration.ACCESS_TOKEN_DURATION_ADMIN.getDuration()))
        .thenReturn("accessToken");

    //when
    ResultActions resultActions = mockMvc.perform(post(API + "/auth/admin/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("admin/login/success",
            requestFields(
                fieldWithPath("username").description("관리자 아이디"),
                fieldWithPath("password").description("비밀번호")
            ),
            responseCookies(
                cookieWithName("accessToken").description("accessToken")
            )
        ));
  }

  @Test
  void 관리자를_로그아웃한다() throws Exception {
    //given
    Cookie accessTokenCookie = new Cookie("accessToken", "Bearer accessToken");

    //when
    ResultActions resultActions = mockMvc.perform(get(API + "/auth/admin/logout")
            .cookie(accessTokenCookie))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(cookie().value("accessToken", ""))
        .andExpect(cookie().maxAge("accessToken", 0))
        .andDo(document("admin/logout/success",
            requestCookies(
                cookieWithName("accessToken").description("accessToken")
            ),
            responseCookies(
                cookieWithName("accessToken").description("accessToken")
            )));

  }

}