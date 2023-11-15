package com.deundeunhaku.reliablekkuserver.order.dto;

import lombok.Getter;

@Getter
public class OrderCalendarResponse {

    private final Integer orderedDay;
    private final Boolean isOrdered;

    private OrderCalendarResponse(Integer orderedDay, Boolean isOrdered) {
        this.orderedDay = orderedDay;
        this.isOrdered = isOrdered;
    }

    public static OrderCalendarResponse of(Integer orderedDay, Boolean isOrdered) {
        return new OrderCalendarResponse(orderedDay, isOrdered);
    }
}
