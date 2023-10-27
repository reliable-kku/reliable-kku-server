package com.deundeunhaku.reliablekkuserver.payment.dto;

import lombok.Data;

@Data
public class PaymentCancelRequest {
    private String cancelReason;
    private Integer cancelAmount;
}
