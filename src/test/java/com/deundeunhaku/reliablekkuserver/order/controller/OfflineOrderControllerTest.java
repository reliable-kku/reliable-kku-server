package com.deundeunhaku.reliablekkuserver.order.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.order.dto.OfflineOrderRequest;
import com.deundeunhaku.reliablekkuserver.order.dto.RegisteredMenuRequest;
import com.deundeunhaku.reliablekkuserver.order.service.OrderService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

class OfflineOrderControllerTest extends BaseControllerTest {

  @MockBean
  private OrderService orderService;

  @Test
  void 오프라인_주문을_등록한다() throws Exception {
    //given
    RegisteredMenuRequest menu1 = RegisteredMenuRequest.of(
        1L,
        3
    );
    RegisteredMenuRequest menu2 = RegisteredMenuRequest.of(
        2L,
        5
    );

    OfflineOrderRequest request = OfflineOrderRequest.of(
        "01012341234",
        10000,
        List.of(menu1, menu2)
    );

    //when
    ResultActions resultActions = mockMvc.perform(post(API + "/offline-orders")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print());

    //then
    resultActions.andExpect(status().isCreated())
        .andDo(document("offline-order/register/success",
            requestFields(
                fieldWithPath("phoneNumber").description("휴대전화 번호"),
                fieldWithPath("totalPrice").description("총 가격"),
                fieldWithPath("registeredMenus[].menuId")
                    .description("각 메뉴 아이디"),
                fieldWithPath("registeredMenus[].count").description("각 메뉴 개수")
            )
        ));
  }

}