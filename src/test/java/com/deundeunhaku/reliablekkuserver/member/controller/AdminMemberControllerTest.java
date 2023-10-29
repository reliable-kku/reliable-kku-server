package com.deundeunhaku.reliablekkuserver.member.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.member.dto.AdminMemberManagementResponse;
import com.deundeunhaku.reliablekkuserver.member.service.AdminMemberService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.ResultActions;

class AdminMemberControllerTest extends BaseControllerTest {

  @MockBean
  AdminMemberService adminMemberService;

  @Test
  void 멤버의_정보들을_리턴한다() throws Exception {
      //given
    String searchKeyword = "test";

    AdminMemberManagementResponse response1 = AdminMemberManagementResponse.of(1L, "test", 1,
        "010-1234-5678");
    AdminMemberManagementResponse response2 = AdminMemberManagementResponse.of(2L, "test2", 3,
        "010-1111-1111");
    AdminMemberManagementResponse response3 = AdminMemberManagementResponse.of(3L, "test3", 2, "010-1234-3456" );

    when(adminMemberService.getMemberList(searchKeyword)).thenReturn(
        List.of(response1, response2, response3));

    //when
    ResultActions resultActions = mockMvc.perform(get(API + "/admin/member")
            .param("searchKeyword", searchKeyword))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("admin/member/list/success",
            queryParameters(
                parameterWithName("searchKeyword").description("검색 키워드")
            ),
            responseFields(
                PayloadDocumentation.fieldWithPath("[].id").description("멤버의 id"),
                PayloadDocumentation.fieldWithPath("[].username").description("멤버의 이름"),
                PayloadDocumentation.fieldWithPath("[].level").description("멤버의 레벨"),
                PayloadDocumentation.fieldWithPath("[].phoneNumber").description("멤버의 전화번호")
            )

        ));
  }

}