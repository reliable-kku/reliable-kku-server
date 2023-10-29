package com.deundeunhaku.reliablekkuserver.member.controller;

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
import org.springframework.data.domain.Pageable;
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

    Pageable pageable = Pageable.ofSize(10);
    pageable.withPage(1);

    when(adminMemberService.getMemberList(searchKeyword, pageable)).thenReturn(
        new PageImpl<>(List.of(response1, response2, response3), pageable, 3
        ));

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
                parameterWithName("page").description("페이지 번호(0부터 시작)"),
                parameterWithName("size").description("페이지 사이즈"),
                parameterWithName("searchKeyword").description("검색 키워드")
            ),
            /**
             *
             {
             "content": [
             {
             "id": 1,
             "username": "test",
             "level": 1,
             "phoneNumber": "010-1234-5678"
             },
             {
             "id": 2,
             "username": "test2",
             "level": 3,
             "phoneNumber": "010-1111-1111"
             },
             {
             "id": 3,
             "username": "test3",
             "level": 2,
             "phoneNumber": "010-1234-3456"
             }
             ],
             "pageable": {
             "pageNumber": 0,
             "pageSize": 10,
             "sort": {
             "empty": true,
             "unsorted": true,
             "sorted": false
             },
             "offset": 0,
             "paged": true,
             "unpaged": false
             },
             "last": true,
             "totalPages": 1,
             "totalElements": 3,
             "first": true,
             "size": 10,
             "number": 0,
             "sort": {
             "empty": true,
             "unsorted": true,
             "sorted": false
             },
             "numberOfElements": 3,
             "empty": false
             }
             *
             */
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
                fieldWithPath("last").description(""),
                fieldWithPath("totalPages").description(""),
                fieldWithPath("totalElements").description("모든 멤버의 개수"),
                fieldWithPath("first").description("첫 페이지인지"),
                fieldWithPath("size").description("한 페이지당 멤버의 개수"),
                fieldWithPath("number").description("현재 페이지 번호"),
                fieldWithPath("numberOfElements").description("현재 페이지의 멤버 개수"),
                fieldWithPath("empty").ignored(),
                fieldWithPath("sort.empty").ignored(),
                fieldWithPath("sort.sorted").ignored(),
                fieldWithPath("sort.unsorted").ignored()
            )
        ));
  }

}