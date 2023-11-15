package com.deundeunhaku.reliablekkuserver.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberRegisterRequest;
import com.deundeunhaku.reliablekkuserver.member.dto.PhoneNumberRequest;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class MemberRegisterControllerTest extends BaseControllerTest {

  @MockBean
  private MemberService memberService;

  @Test
  void 가입된_휴대폰_번호가_없으면_성공을_반환한다() throws Exception {
    //given
    String phoneNumber = "01012345678";

    when(memberService.isPhoneNumberDuplicate(phoneNumber)).thenReturn(false);
    //when
    ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get(API + "/register/phone-number/duplicate")
                .param("phoneNumber", phoneNumber))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("member/register/phone-number/duplicate/success",
            queryParameters(
                parameterWithName("phoneNumber").description("휴대폰 번호")
            ),
            responseFields(
                fieldWithPath("message").description("응답 메시지")
            )
        ));
  }

  @Test
  void 가입된_휴대폰_번호가_있으면_실패를_반환한다() throws Exception {
    //given
    String phoneNumber = "01012345678";

    when(memberService.isPhoneNumberDuplicate(phoneNumber)).thenReturn(true);
    //when
    ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get(API + "/register/phone-number/duplicate")
                .param("phoneNumber", phoneNumber))
        .andDo(print());

    //then
    resultActions.andExpect(status().isBadRequest())
        .andDo(document("member/register/phone-number/duplicate/fail",
            queryParameters(
                parameterWithName("phoneNumber").description("휴대폰 번호")
            ),
            responseFields(
                fieldWithPath("message").description("응답 메시지")
            )
        ));
  }

  @Test
  void 해당_휴대전화로_인증번호를_보낸다() throws Exception {
    //given
    String phoneNumber = "01012341234";
    PhoneNumberRequest request = PhoneNumberRequest.of(phoneNumber);

    //when
    ResultActions resultActions = mockMvc.perform(
            post(API + "/register/phone-number/certification-number")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("member/register/phone-number/certification-number/success",
            requestFields(
                fieldWithPath("phoneNumber").description("휴대전화 번호")
            )
        ));
  }

  @Test
  void 휴대전화_하루_10통_이상보내면_에러를_보낸다() throws Exception {
    //given
    String phoneNumber = "01012341234";
    PhoneNumberRequest request = PhoneNumberRequest.of(phoneNumber);

    doThrow(new IllegalArgumentException("하루에 10번 이상 인증번호를 요청할 수 없습니다."))
        .when(memberService).sendCertificationNumber(any(), any());

    //when
    ResultActions resultActions = mockMvc.perform(
            post(API + "/register/phone-number/certification-number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andDo(print());

    //then
    resultActions.andExpect(status().isBadRequest())
        .andDo(document("member/register/phone-number/certification-number/fail",
            requestFields(
                fieldWithPath("phoneNumber").description("휴대폰 번호")
            ),
            responseFields(
                fieldWithPath("message").description("에러 메시지")
            )
        ));
  }

  @Test
  void 인증번호를_확인한다() throws Exception {
    //given
    String phoneNumber = "01012341234";
    Integer certificationNumber = 123456;

    when(memberService.isCertificationNumberCorrect(phoneNumber, certificationNumber))
        .thenReturn(true);
    //when
    ResultActions resultActions = mockMvc.perform(
            get(API + "/register/phone-number/certification-number")
                .param("phoneNumber", phoneNumber)
                .param("certificationNumber", String.valueOf(certificationNumber)))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("member/register/phone-number/certification-number/check/success",
            queryParameters(
                parameterWithName("phoneNumber").description("휴대폰 번호"),
                parameterWithName("certificationNumber").description("인증번호")
            ),
            responseFields(
                fieldWithPath("message").description("응답 메시지")
            )
        ));

  }

  @Test
  void 인증번호가_일치하지_않는다() throws Exception {
    //given
    String phoneNumber = "01012341234";
    Integer certificationNumber = 123456;

    when(memberService.isCertificationNumberCorrect(phoneNumber, certificationNumber))
        .thenReturn(false);
    //when
    ResultActions resultActions = mockMvc.perform(
            get(API + "/register/phone-number/certification-number")
                .param("phoneNumber", phoneNumber)
                .param("certificationNumber", String.valueOf(certificationNumber)))
        .andDo(print());

    //then
    resultActions.andExpect(status().isBadRequest())
        .andDo(document("member/register/phone-number/certification-number/check/fail",
            queryParameters(
                parameterWithName("phoneNumber").description("휴대폰 번호"),
                parameterWithName("certificationNumber").description("인증번호")
            ),
            responseFields(
                fieldWithPath("message").description("응답 메시지")
            )
        ));

  }

  @Test
  void 회원가입을_성공한다() throws Exception {
    //given
    MemberRegisterRequest request = MemberRegisterRequest.of("홍길동", "01012341234", "password");

    //when
    ResultActions resultActions = mockMvc.perform(post(API + "/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("member/register/success",
            requestFields(
                fieldWithPath("realName").description("실명"),
                fieldWithPath("phoneNumber").description("휴대폰 번호"),
                fieldWithPath("password").description("비밀번호")
            )
        ));
  }

  @Test
  void 회원가입을_실패한다_휴대전화_인증안됨() throws Exception {
    //given
    MemberRegisterRequest request = MemberRegisterRequest.of("홍길동", "01012341234", "password");

    doThrow(new IllegalArgumentException("잘못된 요청입니다."))
        .when(memberService).register(request);
    //when
    ResultActions resultActions = mockMvc.perform(post(API + "/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print());

    //then
    resultActions.andExpect(status().isBadRequest())
        .andDo(document("member/register/fail",
            requestFields(
                fieldWithPath("realName").description("실명"),
                fieldWithPath("phoneNumber").description("휴대폰 번호"),
                fieldWithPath("password").description("비밀번호")
            ),
            responseFields(
                fieldWithPath("message").description("에러 메시지")
            )
        ));
  }

}