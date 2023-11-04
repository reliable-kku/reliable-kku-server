package com.deundeunhaku.reliablekkuserver.payment.repository;

import com.deundeunhaku.reliablekkuserver.payment.domain.Payment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.deundeunhaku.reliablekkuserver.payment.domain.QPayment.payment;


@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Optional<Payment> findPaymentKeyByOrderId(Long orderId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(payment)
                .where(payment.order.id.eq(orderId))
                .fetchOne());

    }
}

