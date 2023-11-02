package com.deundeunhaku.reliablekkuserver.payment.dto;

import com.deundeunhaku.reliablekkuserver.payment.constants.PayType;
import com.deundeunhaku.reliablekkuserver.payment.domain.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    @NotNull
    private PayType payType;

    @NotNull
    private Long amount;

    @NotNull
    private String orderName;

    private String customerName;

    private String successUrl;

    private String failUrl;


    public Payment toEntity(){
        return Payment.builder()
                    .orderId(UUID.randomUUID().toString())
                    .payType(payType)
                    .amount(amount)
                    .orderName(orderName)
                    .paySuccessYn(false)
                    .build();
    }
}
