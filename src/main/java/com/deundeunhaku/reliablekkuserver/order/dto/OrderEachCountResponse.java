package com.deundeunhaku.reliablekkuserver.order.dto;

import com.querydsl.core.annotations.QueryProjection;

public record OrderEachCountResponse(
            Integer waitOrderCount,
            Integer cookingOrderCount,
            Integer finishOrderCount
) {
    public static OrderEachCountResponse of(Integer waitOrderCount, Integer cookingOrderCount, Integer finishOrderCount){
        return new OrderEachCountResponse(waitOrderCount, cookingOrderCount, finishOrderCount);
    }

    @QueryProjection
    public OrderEachCountResponse(Integer waitOrderCount, Integer cookingOrderCount, Integer finishOrderCount) {
        this.waitOrderCount = waitOrderCount;
        this.cookingOrderCount = cookingOrderCount;
        this.finishOrderCount = finishOrderCount;
    }
}
