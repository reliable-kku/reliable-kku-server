package com.deundeunhaku.reliablekkuserver.order.dto;

public record AdminSalesResponse (
    Long realAmount,
    Long paymentCount ,
    Long avgPaymentAmount ,
    Long refundAmount,
    Long refundCount,
    Long avgRefundAmount
){
    public static AdminSalesResponse of(Long realAmount, Long paymentCount, Long avgPaymentAmount, Long refundAmount, Long refundCount, Long avgRefundAmount) {
        return new AdminSalesResponse(realAmount, paymentCount, avgPaymentAmount, refundAmount, refundCount, avgRefundAmount);
    }

}
