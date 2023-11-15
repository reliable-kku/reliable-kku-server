package com.deundeunhaku.reliablekkuserver.payment.repository;

import com.deundeunhaku.reliablekkuserver.payment.domain.Payment;

import java.util.Optional;

public interface PaymentRepositoryCustom {

    Optional<Payment> findPaymentKeyByOrderId(Long orderId);

}
