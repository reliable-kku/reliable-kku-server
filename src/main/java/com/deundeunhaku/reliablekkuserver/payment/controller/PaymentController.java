package com.deundeunhaku.reliablekkuserver.payment.controller;

import com.deundeunhaku.reliablekkuserver.common.config.TossPaymentConfig;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentCancelRequest;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentCancelResponse;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentConfirmRequest;
import com.deundeunhaku.reliablekkuserver.payment.dto.PaymentSuccess;
import com.deundeunhaku.reliablekkuserver.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final TossPaymentConfig tossPaymentConfig;

    // 경주가 일단 결제를 완료하고 여기서 서버에서한번더 재검증 (결제 확인? 완료?)
    @PostMapping("/confirm")
    public ResponseEntity<PaymentSuccess> confirmPayment(@RequestBody @Valid PaymentConfirmRequest request) {

        paymentService.confirmPayment(request.paymentKey(), request.orderId(), request.amount());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<PaymentCancelResponse> cancelPayment(
            @PathVariable Long orderId,
            @RequestBody @Valid PaymentCancelRequest cancelRequest) {
        paymentService.cancelPayment(orderId, cancelRequest);
        return ResponseEntity.ok().build();
    }
}
