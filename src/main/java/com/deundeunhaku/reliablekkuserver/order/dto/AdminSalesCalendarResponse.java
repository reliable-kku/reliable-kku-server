package com.deundeunhaku.reliablekkuserver.order.dto;

import java.util.List;

public record AdminSalesCalendarResponse(
        Integer lastMonthOnMonth,
        Integer totalSalesOfMonth,
        Integer totalRefundSalesOfMonth,
        List<TotalSalesMonthOfDay> total
) {

    public static AdminSalesCalendarResponse of(Integer lastMonthOnMonth, Integer totalSalesOfMonth, Integer totalRefundSalesOfMonth, List<TotalSalesMonthOfDay> total){
        return new AdminSalesCalendarResponse(lastMonthOnMonth, totalSalesOfMonth, totalRefundSalesOfMonth,total);
    }
}
