package com.deundeunhaku.reliablekkuserver.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class AdminSalesResponse {

  private Integer realAmount;
  private Integer paymentCount;
  private Integer avgPaymentAmount;
  private Integer refundAmount;
  private Integer refundCount;
  private Integer avgRefundAmount;

  private AdminSalesResponse(Integer realAmount, Integer paymentCount, Integer avgPaymentAmount,
      Integer refundAmount, Integer refundCount, Integer avgRefundAmount) {
    this.realAmount = realAmount;
    this.paymentCount = paymentCount;
    this.avgPaymentAmount = avgPaymentAmount;
    this.refundAmount = refundAmount;
    this.refundCount = refundCount;
    this.avgRefundAmount = avgRefundAmount;
  }

  public static AdminSalesResponse of(Integer realAmount, Integer paymentCount,
      Integer avgPaymentAmount, Integer refundAmount, Integer refundCount,
      Integer avgRefundAmount) {
    return new AdminSalesResponse(realAmount, paymentCount, avgPaymentAmount, refundAmount,
        refundCount, avgRefundAmount);
  }

  @QueryProjection
  public AdminSalesResponse(Integer realAmount, Integer paymentCount, Integer avgPaymentAmount) {
    this.realAmount = realAmount;
    this.paymentCount = paymentCount;
    this.avgPaymentAmount = avgPaymentAmount;
  }
}
