package com.deundeunhaku.reliablekkuserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String payType;
    private Long amount;
    private String orderId;
    private String orderName;
    private String customerName;
    private String successUrl;
    private String failUrl;

    private String failResponse;
    private boolean cancelYn;
    private String cancelReason;
    private String createAt;
}
