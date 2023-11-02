package com.deundeunhaku.reliablekkuserver.payment.dto;

public record ConfirmPaymentRequestBody(
    String paymentKey,
    String orderId,
    Long amount
) {

  public static ConfirmPaymentRequestBody of(String paymentKey, String orderId, Long amount) {
    return new ConfirmPaymentRequestBody(paymentKey, orderId, amount);
  }

}
