package prajwal.in.util;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.springframework.stereotype.Component;

import prajwal.in.dto.ReportRowDTO;

@Component
public class ExcelGenerator {

    public void generate(HttpServletResponse response, List<ReportRowDTO> records) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Benefit-Report");

        // Header row
        Row header = sheet.createRow(0);
        String[] columns = {"S.No", "Full Name", "Email", "Mobile Number", "Gender", "SSN", "Total Benefit Amount"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        // Data rows
        int rowIndex = 1;
        for (ReportRowDTO dto : records) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(dto.getSerialNo());
            row.createCell(1).setCellValue(dto.getFullName());
            row.createCell(2).setCellValue(dto.getEmail());
            row.createCell(3).setCellValue(dto.getMobileNumber());
            row.createCell(4).setCellValue(dto.getGender());
            row.createCell(5).setCellValue(dto.getSsn());
            row.createCell(6).setCellValue(dto.getTotalBenefitAmount());
        }

        // Send to client
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
