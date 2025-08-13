package prajwal.in.util;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import prajwal.in.dto.ReportRowDTO;

@Component
public class PdfGenerator {

    public void generate(HttpServletResponse response, List<ReportRowDTO> records) throws IOException {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            // Title font
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            fontTitle.setSize(18);
            fontTitle.setColor(Color.BLUE);

            // Title paragraph
            Paragraph title = new Paragraph("Citizen Benefit Report", fontTitle);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Spacer

            // Create table with 7 columns
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100f);
            table.setWidths(new float[] {1f, 3f, 4f, 3f, 2f, 3f, 3f});

            // Header cells
            String[] headers = {"S.No", "Full Name", "Email", "Mobile Number", "Gender", "SSN", "Total Benefit Amount"};
            for (String head : headers) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(head));
                headerCell.setBackgroundColor(Color.LIGHT_GRAY);
                headerCell.setPadding(5);
                table.addCell(headerCell);
            }

            // Data rows
            for (ReportRowDTO dto : records) {
                table.addCell(String.valueOf(dto.getSerialNo()));
                table.addCell(dto.getFullName());
                table.addCell(dto.getEmail());
                table.addCell(dto.getMobileNumber());
                table.addCell(dto.getGender());
                table.addCell(dto.getSsn());
                table.addCell(String.valueOf(dto.getTotalBenefitAmount()));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new IOException("Error generating PDF", e);
        }
    }
}
