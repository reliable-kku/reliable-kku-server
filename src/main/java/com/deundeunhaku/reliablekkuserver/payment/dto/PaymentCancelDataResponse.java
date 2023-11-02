package com.deundeunhaku.reliablekkuserver.payment.dto;

public class PaymentCancelDataResponse {

    Number cancelAmount;
    String cancelReason;
    Number taxFreeAmount;
    Integer taxExemptionAmount;
    Number refundableAmount;
    Number easyPayDiscountAmount;
    String canceledAt;
    String transactionKey;
}
