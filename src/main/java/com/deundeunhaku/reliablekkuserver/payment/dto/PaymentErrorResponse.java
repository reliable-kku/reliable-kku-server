package com.deundeunhaku.reliablekkuserver.payment.dto;

public record PaymentErrorResponse(
    String code,
    String message
) {

}
