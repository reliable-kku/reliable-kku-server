package com.deundeunhaku.reliablekkuserver.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.restdocs.payload.PayloadDocumentation;
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
    AdminMemberManagementResponse response3 = AdminMemberManagementResponse.of(3L, "test3", 2,
        "010-1234-3456");

    PageRequest pageable = PageRequest.of(0, 10);

    when(adminMemberService.getMemberList(eq(searchKeyword), any())).thenReturn(
        new SliceImpl<>(List.of(response1, response2, response3), pageable, true));

    //when
    ResultActions resultActions = mockMvc.perform(get(API + "/admin/member")
            .param("searchKeyword", searchKeyword)
            .param("page", String.valueOf(pageable.getPageNumber()))
            .param("size", String.valueOf(pageable.getPageSize())))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("admin/member/list/success",
            queryParameters(
                parameterWithName("searchKeyword").description("검색 키워드"),
                parameterWithName("page").description("페이지 번호(0부터 시작)"),
                parameterWithName("size").description("페이지 사이즈")
            ),

            responseFields(
                fieldWithPath("content[].id").description("멤버의 id"),
                fieldWithPath("content[].username").description("멤버의 이름"),
                fieldWithPath("content[].level").description("멤버의 레벨"),
                fieldWithPath("content[].phoneNumber").description("멤버의 전화번호"),
                fieldWithPath("pageable.pageNumber").description("페이지 번호"),
                fieldWithPath("pageable.pageSize").description("한페이지당 멤버 개수"),
                fieldWithPath("pageable.sort.empty").ignored(),
                fieldWithPath("pageable.sort.sorted").ignored(),
                fieldWithPath("pageable.sort.unsorted").ignored(),
                fieldWithPath("pageable.offset").ignored(),
                fieldWithPath("pageable.paged").ignored(),
                fieldWithPath("pageable.unpaged").ignored(),
                fieldWithPath("last").description("마지막 페이지인지"),
                fieldWithPath("first").description("첫 페이지인지"),
                fieldWithPath("size").description("한 페이지당 멤버의 개수"),
                fieldWithPath("number").description("현재 페이지 번호"),
                fieldWithPath("numberOfElements").description("현재 페이지의 멤버 개수"),
                fieldWithPath("empty").description("현재 페이지가 비어있는지"),
                fieldWithPath("sort.empty").ignored(),
                fieldWithPath("sort.sorted").ignored(),
                fieldWithPath("sort.unsorted").ignored()
            )
        ));
  }

}