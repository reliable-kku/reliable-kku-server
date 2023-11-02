package com.deundeunhaku.reliablekkuserver.payment.dto;

import com.deundeunhaku.reliablekkuserver.payment.constants.PayType;
import com.deundeunhaku.reliablekkuserver.payment.domain.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class PaymentCancelResponse extends PaymentSuccess{

    List<PaymentCancelDataResponse> cancels;
}
