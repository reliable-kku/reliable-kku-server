package com.deundeunhaku.reliablekkuserver.sse.dto;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SseDataResponse{
    private OrderStatus orderStatus;
    private Long leftMinutes;

    public static SseDataResponse of(OrderStatus orderStatus, Long leftMinutes){
        return new SseDataResponse(orderStatus, leftMinutes);
    }
}
