package com.deundeunhaku.reliablekkuserver.member.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.common.exception.LoginFailedException;
import com.deundeunhaku.reliablekkuserver.jwt.constants.TokenDuration;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.LoginRequest;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class MemberAuthenticationControllerTest extends BaseControllerTest {

  @MockBean
  private MemberService memberService;

  @Test
  void 로그인을_하면_jwt_토큰을_cookie에_담아_반환한다() throws Exception {
    //given
    Long memberId = 1L;
    String phoneNumber = "01012341234";
    String password = "password";
    LoginRequest request = LoginRequest.of(
        phoneNumber,
        password
    );

    Member member = Member.builder()
        .id(1L)
        .realName("realName")
        .phoneNumber(phoneNumber)
        .password(password)
        .build();

    when(jwtTokenUtils.generateJwtToken(phoneNumber,
        TokenDuration.ACCESS_TOKEN_DURATION.getDuration()))
        .thenReturn("accessToken");

    when(jwtTokenUtils.generateJwtToken(phoneNumber,
        TokenDuration.REFRESH_TOKEN_DURATION.getDuration()))
        .thenReturn("refreshToken");

    //when
    ResultActions resultActions = mockMvc.perform(post(API + "/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("member/login/success",
            requestFields(
                fieldWithPath("phoneNumber").description("휴대폰 번호"),
                fieldWithPath("password").description("비밀번호")
            ),
            responseCookies(
                cookieWithName("accessToken").description("accessToken"),
                cookieWithName("refreshToken").description("refreshToken")
            )
        ));
  }

  @Test
  void 휴대폰_비밀번호가_틀리거나_없을시_에러를_던진다() throws Exception {
    //given
    Long memberId = 1L;
    String phoneNumber = "01012341234";
    String password = "password";

    LoginRequest request = LoginRequest.of(
        phoneNumber,
        password
    );
    doThrow(new LoginFailedException())
        .when(memberService).login(phoneNumber, password);

    //when
    ResultActions resultActions = mockMvc.perform(post(API + "/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print());

    //then
    resultActions.andExpect(status().isUnauthorized())
        .andDo(document("member/login/failed",
            requestFields(
                fieldWithPath("phoneNumber").description("휴대폰 번호"),
                fieldWithPath("password").description("비밀번호")
            ),
            responseFields(
                fieldWithPath("message").description("에러 메시지"
                ))));

  }

}