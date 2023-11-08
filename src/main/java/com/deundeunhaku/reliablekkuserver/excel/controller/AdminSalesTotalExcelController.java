package com.deundeunhaku.reliablekkuserver.excel.controller;

import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.ExcelSalesStatisticsResponse;
import com.deundeunhaku.reliablekkuserver.order.repository.MenuOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.Color;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/excel")
    public ResponseEntity<InputStreamResource> downloadExcel(@RequestParam LocalDate startDate, @RequestParam(required = false) LocalDate endDate) throws IOException {

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<Order> orderList = orderRepository.findOrderListByCreatedDateBetween(startDate, endDate);


        SXSSFWorkbook workbook = new SXSSFWorkbook();


        // 데이터를 가져오고 Workbook, Sheet를 만듭니다
        CellStyle greyCellStyle = workbook.createCellStyle();
        applyCellStyle(greyCellStyle, new Color(183, 185, 187));

        CellStyle blueCellStyle = workbook.createCellStyle();
        applyCellStyle(blueCellStyle, new Color(106, 148, 191));

        CellStyle bodyCellStyle = workbook.createCellStyle();
        applyCellStyle(bodyCellStyle, new Color(255, 255, 255));

        Sheet sheet = workbook.createSheet("매출 내역");

        int rowNo = 0;

        Row timeRow = sheet.createRow(rowNo++);

        timeRow.createCell(0).setCellValue("기간: " + startDate + " ~ " + endDate);
        Row nameRow = sheet.createRow(rowNo++);
        nameRow.createCell(0).setCellValue("든붕이");

        rowNo++;

        Row mainRow = sheet.createRow(rowNo++);
        mainRow.createCell(0).setCellValue("총 매출");
        mainRow.createCell(1).setCellValue("결제 건수");
        mainRow.createCell(2).setCellValue("건 단가");
        mainRow.createCell(3).setCellValue("총 환불");
        mainRow.createCell(4).setCellValue("환불 건수");
        for(int i=0; i<=4; i++){
            mainRow.getCell(i).setCellStyle(blueCellStyle);
        }
        Row mainDataRow = sheet.createRow(rowNo++);

        ExcelSalesStatisticsResponse total = orderRepository.findOrderListAllSalesDataByCreateDateBetween(startDate, endDate);

        int onlineTotalSales = total.onlineTotalSales() == null ? 0 : total.onlineTotalSales();
        int offlineTotalSales = total.offlineTotalSales() == null ? 0 : total.offlineTotalSales();
        int refundTotalSales = total.refundTotalSales() == null ? 0 : total.refundTotalSales();
        long totalCount = total.totalCount() == null ? 0 : total.totalCount();
        long refundTotalCount = total.refundTotalCount() == null ? 0 : total.refundTotalCount();
        int orderedSales = total.orderSales() == null ? 0 : total.orderSales();

        int totalAvg = 0;
        if (totalCount - refundTotalCount != 0 || orderedSales - refundTotalSales != 0) {
            totalAvg = (int) ((orderedSales - refundTotalSales) / (totalCount - refundTotalCount));
        }

        mainDataRow.createCell(0).setCellValue(orderedSales);
        mainDataRow.createCell(1).setCellValue(totalCount);
        mainDataRow.createCell(2).setCellValue(totalAvg);
        mainDataRow.createCell(3).setCellValue(refundTotalSales);
        mainDataRow.createCell(4).setCellValue(refundTotalCount);
        for(int i=0; i<=4; i++){
            mainDataRow.getCell(i).setCellStyle(greyCellStyle);
        }

        rowNo++;

        Row headerRow = sheet.createRow(rowNo++);
        headerRow.createCell(0).setCellValue("결제일");
        headerRow.createCell(1).setCellValue("결제 시간");
        headerRow.createCell(2).setCellValue("결제 내역");
        headerRow.createCell(3).setCellValue("합계");
        headerRow.createCell(4).setCellValue("온라인 결제");
        headerRow.createCell(5).setCellValue("오프라인 결제");

        for(int i=0; i<=5; i++){
            headerRow.getCell(i).setCellStyle(blueCellStyle);
        }

        Row totalRow = sheet.createRow(rowNo++);
        int startCell = 0;
        int endCell = 2;
        CellRangeAddress mergedRegion = new CellRangeAddress(totalRow.getRowNum(), totalRow.getRowNum(), startCell, endCell);
        sheet.addMergedRegion(mergedRegion);
        Cell totalCell = totalRow.createCell(startCell);
        totalCell.setCellValue("전체 합계");
        totalCell.setCellStyle(greyCellStyle);
        totalRow.createCell(3).setCellValue(orderedSales);
        totalRow.createCell(4).setCellValue(onlineTotalSales);

        totalRow.createCell(5).setCellValue(offlineTotalSales);

        for(int i=3; i<=5; i++){
            totalRow.getCell(i).setCellStyle(greyCellStyle);
        }

        for (Order order : orderList) {
            Row row = sheet.createRow(rowNo++);


            row.createCell(0).setCellValue(order.getCreatedDate().toString());

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = order.getCreatedAt().toLocalTime().format(timeFormatter);
            row.createCell(1).setCellValue(formattedTime);

            List<String> menuNameList = menuOrderRepository.findMenuNameByEachMenuOrder(order);
            StringBuilder menuName = new StringBuilder();
            for (String name : menuNameList) {
                menuName.append(name).append(",");
            }
            row.createCell(2).setCellValue(menuName.toString());
            row.createCell(3).setCellValue(order.getOrderPrice());
            if (order.getIsOfflineOrder()) {
                row.createCell(4).setCellValue("-");
                row.createCell(5).setCellValue(order.getOrderPrice());
            } else {
                row.createCell(4).setCellValue(order.getOrderPrice());
                row.createCell(5).setCellValue("-");
            }
            for(int i=0; i<=5; i++){
                row.getCell(i).setCellStyle(bodyCellStyle);
            }
        }


            for(int k = 0 ; k < rowNo ; k++) {
                ((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
                sheet.autoSizeColumn(k);
                sheet.setColumnWidth(k, (sheet.getColumnWidth(k))+512);
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

        String fileName = URLEncoder.encode("관리자매출현황" + startDate + endDate + ".xlsx",
            StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentLength(tmpFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment;filename="+fileName) //
                .body(new InputStreamResource(res));

    }

    private void applyCellStyle(CellStyle cellStyle, Color color) {
        XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cellStyle;
        xssfCellStyle.setFillForegroundColor(new XSSFColor(color, new DefaultIndexedColorMap()));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
    }
}