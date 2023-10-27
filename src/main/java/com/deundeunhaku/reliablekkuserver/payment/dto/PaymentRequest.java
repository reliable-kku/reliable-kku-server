package com.deundeunhaku.reliablekkuserver.payment.dto;

import com.deundeunhaku.reliablekkuserver.payment.constants.PayType;
import com.deundeunhaku.reliablekkuserver.payment.domain.Payment;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    @NonNull
    @ApiModelProperty("지불방법")
    private PayType payType;

    @NonNull
    @ApiModelProperty("지불금액")
    private Long amount;

    @NonNull
    @ApiModelProperty("주문 메뉴 이름")
    private String orderName;

    @ApiModelProperty("구매자 이름")
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
