package com.deundeunhaku.reliablekkuserver.payment.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PayType {

    NORMAL("일반 결제"),
    BRANDPAY("브랜드페이 결제"),
    KEYIN("키인 결제");

    private final String payType;
}
