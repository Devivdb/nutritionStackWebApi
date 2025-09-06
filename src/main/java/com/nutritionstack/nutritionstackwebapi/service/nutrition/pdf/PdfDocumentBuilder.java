package com.nutritionstack.nutritionstackwebapi.service.nutrition.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import org.springframework.stereotype.Component;

@Component
public class PdfDocumentBuilder {
    
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9);
    
    public void addTitle(Document document, String title) throws DocumentException {
        Paragraph titleParagraph = new Paragraph(title, TITLE_FONT);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(titleParagraph);
    }
    
    public void addSubtitle(Document document, String subtitle) throws DocumentException {
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph subtitleParagraph = new Paragraph(subtitle, subtitleFont);
        subtitleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitleParagraph);
    }
    
    public void addHeader(Document document, String header) throws DocumentException {
        Paragraph headerParagraph = new Paragraph(header, HEADER_FONT);
        document.add(headerParagraph);
    }
    
    public void addText(Document document, String text) throws DocumentException {
        document.add(new Paragraph(text, NORMAL_FONT));
    }
    
    public void addSpacing(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
    }
    
    public Font getTitleFont() { return TITLE_FONT; }
    public Font getHeaderFont() { return HEADER_FONT; }
    public Font getNormalFont() { return NORMAL_FONT; }
    public Font getSmallFont() { return SMALL_FONT; }
}
