package com.deundeunhaku.reliablekkuserver.order.constant;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {

    WAIT("주문 대기중"),
    COOKING("조리중"),
    COOKED("조리 완료"),
    PICKUP("픽업 완료");

    private final String status; //주문 대기중, 조리중, 조리 완료, 픽업 완료
}
