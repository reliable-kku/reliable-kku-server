package com.deundeunhaku.reliablekkuserver.common.exception;

public enum ExceptionCode {
    INVALID_PAYMENT_AMOUNT("결제 금액 오류가 발생했습니다."),
    PAYMENT_NOT_FOUND("주문번호를 찾을 수 없습니다."),
    ALREADY_APPROVED("이미 결제 승인된 주문입니다."),
    PAYMENT_AMOUNT_EXP("주문 결제 금액이 올바르지 않습니다.");
    private final String message;

    ExceptionCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
