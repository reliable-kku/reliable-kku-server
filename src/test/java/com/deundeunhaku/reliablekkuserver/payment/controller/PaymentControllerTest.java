package com.deundeunhaku.reliablekkuserver.payment.controller;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.common.exception.PaymentCancelException;
import com.deundeunhaku.reliablekkuserver.common.exception.PaymentException;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentCancelRequest;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentConfirmRequest;
import com.deundeunhaku.reliablekkuserver.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest extends BaseControllerTest {

    @MockBean
    private PaymentService paymentService;
    @Test
    void 결제를_승인한다() throws Exception {
        //given
        String paymentKey = "testPaymentKey";
        String orderId = "testOrderId";
        Long amount = 700L;

        PaymentConfirmRequest request = PaymentConfirmRequest.of(paymentKey, orderId, amount);

        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("payments/confirm/success",
                        requestFields(
                                fieldWithPath("paymentKey").description("결제 키 값"),
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("amount").description("결제 금액")
                        )/*,
                        responseFields(
                                fieldWithPath("mId").description("가맹점 Id"),
                                fieldWithPath("version").description("Payment 객체 응답 버전"),
                                fieldWithPath("paymentKey").description("결제 키 값"),
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("orderName").description("주문명"),
                                fieldWithPath("method").description("결제 수단"),
                                fieldWithPath("totalAmount").description("총 결제 금액"),
                                fieldWithPath("balanceAmount").description("취소 가능한 금액"),
                                fieldWithPath("suppliedAmount").description("공급가액"),
                                fieldWithPath("vat").description("부가가치세"),
                                fieldWithPath("status").description("결제 처리 상태"),
                                fieldWithPath("requestedAt").description("결제가 일어난 날짜"),
                                fieldWithPath("approvedAt").description("결제 승인 날짜"),
                                fieldWithPath("useEscrow").description("에스크로 사용 여부"),
                                fieldWithPath("cultureExpense").description("문화비"),
                                fieldWithPath("card").description("카드 관련 정보"),
                                fieldWithPath("type").description("결제 타입")
                        )*/
                        ));

    }

    @Test
    void 결제를_실패한다() throws Exception {
        //given
        String paymentKey;
        paymentKey = null;
        String orderId = null;
        Long amount = 700L;

        PaymentConfirmRequest request = PaymentConfirmRequest.of(paymentKey, orderId, amount);

        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("payments/confirm/fail",
                        requestFields(
                                fieldWithPath("paymentKey").description("결제 키 값"),
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("amount").description("결제 금액")
                        ),
                        responseFields(
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));
    }

    @Test
    void 토스서버_에러() throws Exception {
        //given
        String paymentKey = "testPaymentKey";
        String orderId = "testOrderId";
        Long amount = 700L;

        PaymentConfirmRequest request = PaymentConfirmRequest.of(paymentKey, orderId, amount);

        Mockito.doThrow(new PaymentException()).when(paymentService)
                .confirmPayment(any(), any(), any());

        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("payments/confirm/tossErrors",
                        requestFields(
                                fieldWithPath("paymentKey").description("결제 키 값"),
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("amount").description("결제 금액")
                        ),
                        responseFields(
                         fieldWithPath("message").description("에러 메시지")
                        )
                ));

    }

    @Test
    void 결제를_취소한다() throws Exception {
        //given
        Long orderId = 1L;
        PaymentCancelRequest request = PaymentCancelRequest.of("취소이유입니다");
        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/payments/{orderId}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("payments/cancel/success",
                        pathParameters(
                                parameterWithName("orderId").description("주문 ID")
                        ),
                        requestFields(
                                fieldWithPath("cancelReason").description("취소 사유")
                        )));
    }

    @Test
    void 결제_취소를_실패한다() throws Exception {
        //given
        Long orderId = 1L;
        PaymentCancelRequest request = PaymentCancelRequest.of(null);
        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/payments/{orderId}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("payments/cancel/fail",
                        pathParameters(
                                parameterWithName("orderId").description("주문 ID")
                        ),
                        requestFields(
                                fieldWithPath("cancelReason").description("취소 사유")
                        ),
                        responseFields(
                                fieldWithPath("message").description("에러 메시지")
                        )));
    }

    @Test
    void 결제_취소시_토스_서버에러() throws Exception {
        //given
        Long orderId = 1L;
        PaymentCancelRequest request = PaymentCancelRequest.of("cancelReasonTest");

        Mockito.doThrow(new PaymentCancelException()).when(paymentService)
                .cancelPayment(any(), any());

        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/payments/{orderId}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("payments/cancel/tossErrors",
                        pathParameters(
                                parameterWithName("orderId").description("주문 ID")
                        ),
                        requestFields(
                                fieldWithPath("cancelReason").description("취소 사유")
                        ),
                        responseFields(
                                fieldWithPath("message").description("에러 메시지")
                        )));
    }

}