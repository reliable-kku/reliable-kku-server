package com.deundeunhaku.reliablekkuserver.order.constant;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {

  WAIT("주문 대기중"),
  COOKING("조리중"),
  PICKUP("조리 완료"),
  FINISH("픽업 완료"),
  CANCELED("주문 취소"),
  NOT_TAKE("픽업 안함");

  private final String status;
}
