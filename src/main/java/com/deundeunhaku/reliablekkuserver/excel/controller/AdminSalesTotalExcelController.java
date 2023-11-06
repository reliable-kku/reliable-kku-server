package com.deundeunhaku.reliablekkuserver.excel.controller;

import com.deundeunhaku.reliablekkuserver.menu.repository.MenuRepository;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.ExcelSalesStatisticsResponse;
import com.deundeunhaku.reliablekkuserver.order.repository.MenuOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminSalesTotalExcelController {

    private final OrderRepository orderRepository;
    private final MenuOrderRepository menuOrderRepository;
    private final MenuRepository menuRepository;


    @GetMapping("/excel")
    public ResponseEntity<InputStreamResource> downloadExcel(HttpServletResponse response, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) throws IOException {

//        List<Order> orderList = orderRepository.findAll();
        List<Order> orderList = orderRepository.findOrderListByCreatedDateBetween(startDate, endDate);


        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("매출 내역");

        int rowNo = 0;

        Row timeRow = sheet.createRow(rowNo++);

        /*LocalDate firstOrderDate = orderList.stream()
                .map(Order::getCreatedDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
        LocalDate lastOrderDate = orderList.stream()
                .map(Order::getCreatedDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());*/

        timeRow.createCell(0).setCellValue("기간: " + startDate + " ~ " + endDate);

        Row nameRow = sheet.createRow(rowNo++);
        nameRow.createCell(0).setCellValue("든붕이");

        //빈값
        rowNo++;

        Row mainRow = sheet.createRow(rowNo++);
        mainRow.createCell(0).setCellValue("총 매출");
        mainRow.createCell(1).setCellValue("결제 건수");
        mainRow.createCell(2).setCellValue("건 단가");
        mainRow.createCell(3).setCellValue("총 환불");
        mainRow.createCell(4).setCellValue("환불 건수");

        Row mainDataRow = sheet.createRow(rowNo++);

        ExcelSalesStatisticsResponse total = orderRepository.findOrderListAllSalesDataByCreateDateBetween(startDate, endDate);
        Integer totalAvg = (int) ((total.orderSales() - total.refundTotalSales()) / (total.totalCount() - total.refundTotalCount()));

        mainDataRow.createCell(0).setCellValue(total.orderSales());
        mainDataRow.createCell(1).setCellValue(total.totalCount());
        mainDataRow.createCell(2).setCellValue(totalAvg);
        mainDataRow.createCell(3).setCellValue(total.refundTotalSales());
        mainDataRow.createCell(4).setCellValue(total.refundTotalCount());
        /*double totalSales = 0.0;
        int totalOrders = 0;
        double totalRefund = 0.0;
        int totalRefundOrders = 0;
        for(Order order : orderList){
            totalSales += order.getOrderPrice();
            totalOrders++;
            if(order.getOrderStatus() == OrderStatus.CANCELED){
                totalRefund +=
            }
        }

        mainDataRow.createCell(0).setCellValue();*/

        //빈값
        rowNo++;

        Row headerRow = sheet.createRow(rowNo++);
        headerRow.createCell(0).setCellValue("결제일");
        headerRow.createCell(1).setCellValue("결제 시간");
        headerRow.createCell(2).setCellValue("결제 내역");
        headerRow.createCell(3).setCellValue("합계");
        headerRow.createCell(4).setCellValue("온라인 결제");
        headerRow.createCell(5).setCellValue("오프라인 결제");

        Row totalRow = sheet.createRow(rowNo++);
        int startCell = 0;
        int endCell = 2;
        CellRangeAddress mergedRegion = new CellRangeAddress(totalRow.getRowNum(), totalRow.getRowNum(), startCell, endCell);
        sheet.addMergedRegion(mergedRegion);
        Cell totalCell = totalRow.createCell(startCell);
        totalCell.setCellValue("전체 합계");
        totalRow.createCell(3).setCellValue(total.orderSales());
        totalRow.createCell(4).setCellValue(total.onlineTotalSales());
        totalRow.createCell(5).setCellValue(total.offlineTotalSales());


        for (Order order : orderList) {
            Row row = sheet.createRow(rowNo++);
            row.createCell(0).setCellValue(order.getCreatedDate());

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = order.getCreatedAt().toLocalTime().format(timeFormatter);
            row.createCell(1).setCellValue(formattedTime);

            List<String> menuNameList = menuOrderRepository.findMenuNameByEachMenuOrder(order);
            String menuName = "";
            for (String name : menuNameList) {
                menuName += name + ",";
                ;
            }
            row.createCell(2).setCellValue(menuName);
            row.createCell(3).setCellValue(order.getOrderPrice());
            if (order.getIsOfflineOrder()) {
                row.createCell(4).setCellValue("-");
                row.createCell(5).setCellValue(order.getOrderPrice());
            } else {
                row.createCell(4).setCellValue(order.getOrderPrice());
                row.createCell(5).setCellValue("-");
            }


        }


        File tmpFile = File.createTempFile("TMP~", ".xlsx");
        try (OutputStream fos = new FileOutputStream(tmpFile);) {
            workbook.write(fos);
        }
        InputStream res = new FileInputStream(tmpFile) {
            @Override
            public void close() throws IOException {
                super.close();
                if (tmpFile.delete()) {
                    log.info("임시 파일 삭제 완료");
                }
            }
        };

        return ResponseEntity.ok()
                .contentLength(tmpFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment;filename=adminSalesTotalList.xlsx") //
                .body(new InputStreamResource(res));

    }
}