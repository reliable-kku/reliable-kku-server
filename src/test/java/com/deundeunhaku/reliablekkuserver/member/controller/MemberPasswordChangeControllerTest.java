package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberPasswordChangeRequest;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberPasswordChangeControllerTest extends BaseControllerTest {

    @MockBean
    MemberService memberService;

    @Test
    void 현재비밀번호와_작성한_비밀번호가_같은경우_true를_리턴한다() throws Exception{
        //given
        final String password = "password";

        when(memberService.isMemberPasswordMatch(any(), anyString()))
                .thenReturn(true);
        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/my-pages/change-password/verify-current-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\""+password+"\"}"))
                .andDo(print());
        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("my-pages/change-password/verify-current-password/success",
                                requestFields(
                                        fieldWithPath("password").description("검사할 현재 비밀번호")
                                ),
                                responseFields(
                                        fieldWithPath("isPasswordMatch").description("패스워드가 일치하는지 여부")
                                )
                        )
                );
    }

    @Test
    void 사용자의_비밀번호를_변경한다() throws Exception {
        //given
        final MemberPasswordChangeRequest request = MemberPasswordChangeRequest.of( "password");

        when(memberService.changeMemberPassword(any(), eq(request)))
                .thenReturn(true);
        //when
        ResultActions resultActions = mockMvc.perform(patch(API + "/my-pages/change-password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("my-pages/change-password/success",
                        requestFields(
                                fieldWithPath("password").description("새 비밀번호")
                        )
                ));
    }

}