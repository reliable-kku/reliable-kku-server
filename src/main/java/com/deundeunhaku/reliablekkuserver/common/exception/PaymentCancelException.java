package com.deundeunhaku.reliablekkuserver.common.exception;

import lombok.Getter;

@Getter
public class PaymentCancelException extends RuntimeException{

    public PaymentCancelException() {super("결제 취소 중 오류가 발생했습니다.");}

    public PaymentCancelException(String message){super (message);}
}
