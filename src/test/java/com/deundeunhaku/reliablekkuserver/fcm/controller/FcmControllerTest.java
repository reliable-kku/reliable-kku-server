package com.deundeunhaku.reliablekkuserver.fcm.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.fcm.dto.FcmTokenRequest;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

class FcmControllerTest extends BaseControllerTest {

  @MockBean
  private MemberService memberService;

  @Test
  void 멤버의_firebaseToken을_저장한다() throws Exception {
    //given
    FcmTokenRequest request = FcmTokenRequest.of("firebaseToken");

    doNothing().when(memberService).updateFcmToken(any(), any());

    //when
    ResultActions resultActions = mockMvc.perform(post(API + "/fcm")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print());

    //then
    resultActions
        .andExpect(status().isOk())
        .andDo(document("fcm/update",
            requestFields(
                fieldWithPath("token").description("firebase token")
            )));

  }

}