package com.deundeunhaku.reliablekkuserver.payment.dto;

import lombok.Data;

@Data
public class PaymentSuccessCard {

    String company; //카드 회사명
    String number; //카드 번호
    String installmentPlanMonths; //할부 개월
    String isInterestFree;
    String approveNo; //승인번호
    String useCardPoing; //카드 포인트 사용 여부
    String cardType; //카드 타입
    String ownerType; //소유자 타입
    String acquireStatus; //승인 상태
    String receiptUrl; //영수증 Url
}
