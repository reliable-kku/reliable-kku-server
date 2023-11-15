package com.deundeunhaku.reliablekkuserver.payment.dto;


import jakarta.validation.constraints.NotNull;

public record PaymentCancelRequest(
        @NotNull String cancelReason
) {
    public static PaymentCancelRequest of(String cancelReason){
        return new PaymentCancelRequest(cancelReason);
    }
}
