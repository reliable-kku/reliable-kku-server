package com.deundeunhaku.reliablekkuserver.payment.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentCancelResponse {

  private String mId;// 가맹점 Id -> tosspayments
  private String lastTransactionKey;
  private String paymentKey;
  private String orderId;
  private String orderName;
  private Integer taxExemptionAmount;
  private String status;
  private String requestedAt;
  private String approvedAt;
  private Boolean useEscrow;
  private Boolean cultureExpense;
  private PaymentCardResponse card;
  private List<PaymentCancelDataResponse> cancels;
  private String type;
  private EasyPay easyPay;
  private String country;
  private Boolean isPartialCancelable;
  private Receipt receipt;
  private Checkout checkout;
  private String currency;
  private Integer totalAmount;
  private Integer balanceAmount;
  private Integer suppliedAmount;
  private Integer vat;
  private Integer taxFreeAmount;
  private String method;
  private String version;

//  {
//  "mId": "tvivarepublica",
//  "lastTransactionKey": "4C9392A789096E4ED5866CDC757A7230",
//  "paymentKey": "5EnNZRJGvaBX7zk2yd8y5xE5AD4JM08x9POLqKQjmAw4b0e1",
//  "orderId": "ca40f7b1-4319-436d-923e-c1d6be38b9ef",
//  "orderName": "든붕이",
//  "taxExemptionAmount": 0,
//  "status": "CANCELED",
//  "requestedAt": "2023-11-08T03:57:53+09:00",
//  "approvedAt": "2023-11-08T03:58:18+09:00",
//  "useEscrow": false,
//  "cultureExpense": false,
//  "card": null,
//  "virtualAccount": null,
//  "transfer": null,
//  "mobilePhone": null,
//  "giftCertificate": null,
//  "cashReceipt": null,
//  "cashReceipts": null,
//  "discount": null,
//  "cancels": [
//    {
//      "transactionKey": "4C9392A789096E4ED5866CDC757A7230",
//      "cancelReason": "사용자 취소",
//      "taxExemptionAmount": 0,
//      "canceledAt": "2023-11-08T03:58:59+09:00",
//      "easyPayDiscountAmount": 36,
//      "receiptKey": null,
//      "cancelAmount": 1400,
//      "taxFreeAmount": 0,
//      "refundableAmount": 0
//    }
//  ],
//  "secret": null,
//  "type": "NORMAL",
//  "easyPay": {
//    "provider": "토스페이",
//    "amount": 1364,
//    "discountAmount": 36
//  },
//  "country": "KR",
//  "failure": null,
//  "isPartialCancelable": true,
//  "receipt": {
//    "url": "https://dashboard.tosspayments.com/receipt/redirection?transactionId=tviva20231108035753MHLZ1&ref=PX"
//  },
//  "checkout": {
//    "url": "https://api.tosspayments.com/v1/payments/5EnNZRJGvaBX7zk2yd8y5xE5AD4JM08x9POLqKQjmAw4b0e1/checkout"
//  },
//  "currency": "KRW",
//  "totalAmount": 1400,
//  "balanceAmount": 0,
//  "suppliedAmount": 0,
//  "vat": 0,
//  "taxFreeAmount": 0,
//  "method": "간편결제",
//  "version": "2022-11-16"
//}

//    private String mId;// 가맹점 Id -> tosspayments
//    private String version; // Payment 객체 응답 버전
//    private String paymentKey;
//    private String orderId;
//    private String orderName;
//    private String method; // 결제 수단
//    private String totalAmount;
//    private String balanceAmount;
//    private String suppliedAmount;
//    private String vat; // 부가가치세
//    private String status; // 결제 처리 상태
//    private String requestedAt;
//    private String approvedAt;
//    private String useEscrow; // false
//    private String cultureExpense; // false
//    private PaymentSuccessCard card; // 결제 카드 정보 (아래 자세한 정보 있음)
//    private PayType type; // 결제
//    private List<PaymentCancelDataResponse> cancels;

}
