package com.deundeunhaku.reliablekkuserver.common.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

  public PaymentException() {
    super("결제 중 오류가 발생했습니다.");
  }

  public PaymentException(String message) {
    super(message);
  }
}

