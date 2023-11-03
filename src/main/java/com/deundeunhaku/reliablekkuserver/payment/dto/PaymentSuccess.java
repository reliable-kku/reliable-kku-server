package com.deundeunhaku.reliablekkuserver.payment.dto;

import com.deundeunhaku.reliablekkuserver.payment.constants.PayType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentSuccess {

  private String mId;// 가맹점 Id -> tosspayments
  private String version; // Payment 객체 응답 버전
  private String paymentKey;
  private String orderId;
  private String orderName;
  private String method; // 결제 수단
  private String totalAmount;
  private String balanceAmount;
  private String suppliedAmount;
  private String vat; // 부가가치세
  private String status; // 결제 처리 상태
  private String requestedAt;
  private String approvedAt;
  private String useEscrow; // false
  private String cultureExpense; // false
  private PaymentSuccessCard card; // 결제 카드 정보 (아래 자세한 정보 있음)
  private PayType type; // 결제

}
