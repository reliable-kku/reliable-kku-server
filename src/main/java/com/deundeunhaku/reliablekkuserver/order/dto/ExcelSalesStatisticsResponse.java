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
        return new ExcelSalesStatisticsResponse(
            orderSales == null ? 0 : orderSales,
            totalCount == null ? 0 : totalCount,
            refundTotalSales == null ? 0 : refundTotalSales,
            refundTotalCount == null ? 0 : refundTotalCount,
            onlineTotalSales == null ? 0 : onlineTotalSales,
            offlineTotalSales == null ? 0 : offlineTotalSales
        );
    }


    @QueryProjection
    public ExcelSalesStatisticsResponse {
    }
}
