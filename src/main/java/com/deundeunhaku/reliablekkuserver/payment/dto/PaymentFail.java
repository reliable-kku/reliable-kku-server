package com.deundeunhaku.reliablekkuserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFail {
    String errorCode;
    String errorMessage;
    String orderId;
}
