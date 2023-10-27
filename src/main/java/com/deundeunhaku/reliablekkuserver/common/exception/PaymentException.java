package com.deundeunhaku.reliablekkuserver.common.exception;

public class PaymentException extends RuntimeException {
    private final ExceptionCode exceptionCode;
/*
    public PaymentException(){ super("결제 중 오류가 발생했습니다.");}
*/
    public PaymentException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }}
