package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.MemberMyPageResponse;
import com.deundeunhaku.reliablekkuserver.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


class MemberMyPageControllerTest extends BaseControllerTest {

    @MockBean
    private MemberService memberService;
    @Test
    void 마이페이지에_회원의_이름과_레벨을_넘긴다() throws Exception{
        //given
        MemberMyPageResponse response = MemberMyPageResponse.of("MJ", 1);
        when(memberService.getMyPageInfo(any())).thenReturn(response);

        //when
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(API + "/my-pages/member")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                        document("my-pages/member/information",
                             responseFields(
                                      fieldWithPath("realName").description("이름"),
                                      fieldWithPath("level").description("레벨")
                        )));
    }
}