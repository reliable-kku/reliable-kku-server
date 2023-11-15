package com.deundeunhaku.reliablekkuserver.order.dto;

import com.querydsl.core.annotations.QueryProjection;

public record TotalSalesMonthOfDay(
        Integer totalSales,
        Integer refundTotalSales
) {
    @QueryProjection
    public TotalSalesMonthOfDay(Integer totalSales, Integer refundTotalSales) {
        this.totalSales = totalSales;
        this.refundTotalSales = refundTotalSales;
    }
    public static TotalSalesMonthOfDay of(Integer totalSales, Integer refundTotalSales){
        return new TotalSalesMonthOfDay(totalSales, refundTotalSales);
    }
}
