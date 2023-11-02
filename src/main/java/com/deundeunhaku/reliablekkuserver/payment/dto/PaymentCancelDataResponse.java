package com.deundeunhaku.reliablekkuserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentCancelDataResponse {

  private Number cancelAmount;
  private String cancelReason;
  private Number taxFreeAmount;
  private Integer taxExemptionAmount;
  private Number refundableAmount;
  private Number easyPayDiscountAmount;
  private String canceledAt;
  private String transactionKey;
}
