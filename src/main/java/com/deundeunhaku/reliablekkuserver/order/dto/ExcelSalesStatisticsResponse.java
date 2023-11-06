package com.deundeunhaku.reliablekkuserver.order.dto;

import com.querydsl.core.annotations.QueryProjection;

public record ExcelSalesStatisticsResponse(
        Integer orderSales,
        Long totalCount,
        Integer refundTotalSales,
        Long refundTotalCount,
        Integer onlineTotalSales,
        Integer offlineTotalSales) {

    public static ExcelSalesStatisticsResponse of (Integer orderSales, Long totalCount, Integer refundTotalSales, Long refundTotalCount,Integer onlineTotalSales,Integer offlineTotalSales ){
        return new ExcelSalesStatisticsResponse(orderSales, totalCount, refundTotalSales, refundTotalCount, onlineTotalSales, offlineTotalSales);
    }


    @QueryProjection
    public ExcelSalesStatisticsResponse {
    }
}
