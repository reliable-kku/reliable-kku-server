package com.deundeunhaku.reliablekkuserver.payment.dto;

import com.deundeunhaku.reliablekkuserver.payment.constants.PayType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentSuccess {

  private String mId;// 가맹점 Id -> tosspayments
  private String version; // Payment 객체 응답 버전
  private String lastTransactionKey;
  private String paymentKey;
  private String orderId;
  private String orderName;
  private String currency;
  private String method; // 결제 수단
  private String status;
  private LocalDateTime requestedAt;
  private LocalDateTime approvedAt;
  private Boolean useEscrow;
  private Boolean cultureExpense;
  private PaymentCardResponse card;
  private String virtualAccount;
  private String transfer;
  private String mobilePhone;
  private String giftCertificate;
  private String foreignEasyPay;
  private String cashReceipt;
  private String cashReceipts;
  private Receipt receipt;
  private Checkout checkout;
  private String discount;
  private String cancels;
  private String secret;
  private PayType type;
  private String easyPay;
  private String country;
  private String failure;
  private Integer totalAmount;
  private Integer balanceAmount;
  private Integer suppliedAmount;
  private Integer vat;
  private Integer taxFreeAmount;
  private Integer taxExemptionAmount;

}

@AllArgsConstructor
@Getter
class PaymentCardResponse {

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

@AllArgsConstructor
@Getter
class Receipt {

  private String url;
}

@AllArgsConstructor
@Getter
class Checkout {

  private String url;
}
