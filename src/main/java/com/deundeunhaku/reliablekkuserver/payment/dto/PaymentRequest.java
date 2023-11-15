package com.deundeunhaku.reliablekkuserver.payment.dto;

import com.deundeunhaku.reliablekkuserver.payment.constants.PayType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

}
