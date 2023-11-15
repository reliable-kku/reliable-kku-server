package com.deundeunhaku.reliablekkuserver.payment.dto;

import com.deundeunhaku.reliablekkuserver.payment.constants.PayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


@AllArgsConstructor
@NoArgsConstructor
@Data
class PaymentCardResponse{
    private String amount;
    private String issuerCode;
    private String acquirerCode;
    private String number;
    private Integer installmentPlanMonths;
    private Boolean isInterestFree;
    private String interestPayer;
    private String approveNo;
    private Boolean useCardPoint;
    private String cardType;
    private String ownerType;
    private String acquireStatus;
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