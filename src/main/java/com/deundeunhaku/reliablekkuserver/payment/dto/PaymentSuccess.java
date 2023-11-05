package com.deundeunhaku.reliablekkuserver.payment.dto;

import com.deundeunhaku.reliablekkuserver.payment.constants.PayType;

public record PaymentSuccess(
    String mid,
    String lastTransactionKey,
    String paymentKey,
    String orderId,
    String orderName,
    Integer taxExemptionAmount,
    String status,
    String requestedAt,
    String approvedAt,
    Boolean useEscrow,
    Boolean cultureExpense,
    PaymentCardResponse card,
    String secret,
    PayType type,
    EasyPay easyPay,
    String country,
    Boolean isPartialCancelable,
    Receipt receipt,
    Checkout checkout,
    String currency,
    Integer totalAmount,
    Integer balanceAmount,
    Integer suppliedAmount,
    Integer vat,
    Integer taxFreeAmount,
    String method,
    String version
) {

}

//   String mId,// 가맹점 Id -> tosspayments
//   String version, // Payment 객체 응답 버전
//   String lastTransactionKey,
//   String paymentKey,
//   String orderId,
//   String orderName,
//   String currency,
//   String method, // 결제 수단
//   String status,
//   LocalDateTime requestedAt,
//   LocalDateTime approvedAt,
//   Boolean useEscrow,
//   Boolean cultureExpense,
// PaymentCardResponse card,
////   String virtualAccount,
////   String transfer,
////   String mobilePhone,
////   String giftCertificate,
////   String foreignEasyPay,
////   String cashReceipt,
////   String cashReceipts,
////   Receipt receipt,
////   Checkout checkout,
////   String discount,
////   String cancels,
////   String secret,
//   PayType type,
//   EasyPay easyPay,
//   String country,
////   String failure,
//   Integer totalAmount,
//   Integer balanceAmount,
//   Integer suppliedAmount,
//   Integer vat,
//   Integer taxFreeAmount,
//   Integer taxExemptionAmount


record PaymentCardResponse(
    String amount,
    String issuerCode,
    String acquirerCode,
    String number,
    Integer installmentPlanMonths,
    Boolean isInterestFree,
//  String interestPayer,
    String approveNo,
    Boolean useCardPoint,
    String cardType,
    String ownerType,
    String acquireStatus
) {

}

record Receipt(
    String url
) {

}

record Checkout(
    String url
) {

}

record EasyPay(
    String provider,
    Integer amount,
    Integer discountAmount
) {

}