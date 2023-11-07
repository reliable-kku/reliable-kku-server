package com.deundeunhaku.reliablekkuserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentCancelDataResponse {

  private Integer cancelAmount;
  private String cancelReason;
  private Integer taxFreeAmount;
  private Integer taxExemptionAmount;
  private Integer refundableAmount;
  private Integer easyPayDiscountAmount;
  private String canceledAt;
  private String transactionKey;
}
