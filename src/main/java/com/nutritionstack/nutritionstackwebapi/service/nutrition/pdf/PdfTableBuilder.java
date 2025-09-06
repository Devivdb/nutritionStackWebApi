package com.nutritionstack.nutritionstackwebapi.service.nutrition.pdf;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.springframework.stereotype.Component;

@Component
public class PdfTableBuilder {
    
    public PdfPTable createTable(int columns) {
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(100);
        return table;
    }
    
    public void addTableHeader(PdfPTable table, Font headerFont, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
            table.addCell(cell);
        }
    }
    
    public void addTableRow(PdfPTable table, Font font, String... values) {
        for (String value : values) {
            table.addCell(new PdfPCell(new Phrase(value, font)));
        }
    }
    
    public void addProgressRow(PdfPTable table, Font font, String nutrient, String current, String goal, String progress) {
        table.addCell(new PdfPCell(new Phrase(nutrient, font)));
        table.addCell(new PdfPCell(new Phrase(current, font)));
        table.addCell(new PdfPCell(new Phrase(goal, font)));
        table.addCell(new PdfPCell(new Phrase(progress, font)));
    }
}
