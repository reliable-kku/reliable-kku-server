package com.deundeunhaku.reliablekkuserver.payment.repository;

import com.deundeunhaku.reliablekkuserver.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {
    Optional<Payment> findByTossOrderId(String tossOrderId);

    Optional<Object> findByPaymentKey(String paymentKey);
}
