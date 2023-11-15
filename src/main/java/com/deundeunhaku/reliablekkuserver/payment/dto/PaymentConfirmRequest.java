package com.deundeunhaku.reliablekkuserver.payment.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentConfirmRequest(
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount
) {

    public static PaymentConfirmRequest of(String paymentKey, String orderId, Long amount) {
        return new PaymentConfirmRequest(paymentKey, orderId, amount);
    }
}
