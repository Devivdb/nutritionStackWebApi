package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import com.nutritionstack.nutritionstackwebapi.model.nutrition.NutritionInfo;
import com.nutritionstack.nutritionstackwebapi.model.product.Product;
import com.nutritionstack.nutritionstackwebapi.model.tracking.LoggedProduct;
import com.nutritionstack.nutritionstackwebapi.repository.product.ProductRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PdfReportGeneratorService {
    
    private final ProductRepository productRepository;
    
    public PdfReportGeneratorService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public byte[] generateNutritionReport(PdfReportRequest request) throws IOException {
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("NUTRITION REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Font reportIdFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph reportIdParagraph = new Paragraph("Report ID: " + request.getReportId(), reportIdFont);
            reportIdParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(reportIdParagraph);
            document.add(new Paragraph(" "));
            
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            Paragraph userInfo = new Paragraph("User Information:", headerFont);
            document.add(userInfo);
            document.add(new Paragraph("Username: " + request.getUser().getUsername(), normalFont));
            document.add(new Paragraph("Report Type: " + request.getReportType(), normalFont));
            document.add(new Paragraph("Period: " + formatDate(request.getStartDate()) + " to " + formatDate(request.getEndDate()), normalFont));
            document.add(new Paragraph("Generated: " + formatDateTime(LocalDateTime.now()), normalFont));
            document.add(new Paragraph(" "));
            
            if (request.getUserGoal() != null && request.getNutritionSummary() != null && request.getProgressSummary() != null) {
                document.add(new Paragraph("Progress Summary", headerFont));
                document.add(new Paragraph(" "));
                
                PdfPTable progressTable = new PdfPTable(4);
                progressTable.setWidthPercentage(100);
                
                addTableHeader(progressTable, "Nutrient", "Current", "Goal", "Progress");
                
                addProgressRow(progressTable, "Calories", 
                    String.format("%.1f kcal", request.getNutritionSummary().getTotalNutrition().getCalories()),
                    String.format("%.1f kcal", request.getUserGoal().getCaloriesGoal()),
                    String.format("%.1f%%", request.getProgressSummary().getCaloriesProgress()));
                    
                addProgressRow(progressTable, "Protein", 
                    String.format("%.1f g", request.getNutritionSummary().getTotalNutrition().getProtein()),
                    String.format("%.1f g", request.getUserGoal().getProteinGoal()),
                    String.format("%.1f%%", request.getProgressSummary().getProteinProgress()));
                    
                addProgressRow(progressTable, "Carbs", 
                    String.format("%.1f g", request.getNutritionSummary().getTotalNutrition().getCarbs()),
                    String.format("%.1f g", request.getUserGoal().getCarbsGoal()),
                    String.format("%.1f%%", request.getProgressSummary().getCarbsProgress()));
                    
                addProgressRow(progressTable, "Fat", 
                    String.format("%.1f g", request.getNutritionSummary().getTotalNutrition().getFat()),
                    String.format("%.1f g", request.getUserGoal().getFatGoal()),
                    String.format("%.1f%%", request.getProgressSummary().getFatProgress()));
                
                document.add(progressTable);
                document.add(new Paragraph(" "));
            }
            
            if (request.getNutritionSummary() != null) {
                document.add(new Paragraph("Total Nutrition Summary", headerFont));
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Total Products Logged: " + request.getNutritionSummary().getTotalProducts(), normalFont));
                
                NutritionInfo total = request.getNutritionSummary().getTotalNutrition();
                document.add(new Paragraph("Calories: " + String.format("%.1f kcal", total.getCalories()), normalFont));
                document.add(new Paragraph("Protein: " + String.format("%.1f g", total.getProtein()), normalFont));
                document.add(new Paragraph("Carbs: " + String.format("%.1f g", total.getCarbs()), normalFont));
                document.add(new Paragraph("Fat: " + String.format("%.1f g", total.getFat()), normalFont));
                document.add(new Paragraph("Fiber: " + String.format("%.1f g", total.getFiber()), normalFont));
                document.add(new Paragraph("Sugar: " + String.format("%.1f g", total.getSugar()), normalFont));
                document.add(new Paragraph("Salt: " + String.format("%.1f g", total.getSalt()), normalFont));
                document.add(new Paragraph(" "));
            }
            
            document.add(new Paragraph("Nutrition by Meal Type", headerFont));
            document.add(new Paragraph(" "));
            
            if (request.getLoggedProducts().isEmpty()) {
                document.add(new Paragraph("No logged products found for the specified date range.", normalFont));
            } else {
                Map<String, Product> productMap = new HashMap<>();
                for (LoggedProduct loggedProduct : request.getLoggedProducts()) {
                    try {
                        productRepository.findByEan13Code(loggedProduct.getEan13Code())
                            .ifPresent(product -> productMap.put(loggedProduct.getEan13Code(), product));
                    } catch (Exception e) {
                        System.err.println("Error fetching product " + loggedProduct.getEan13Code() + ": " + e.getMessage());
                    }
                }
                
                Map<String, List<LoggedProduct>> productsByMealType = groupProductsByMealType(request.getLoggedProducts());
                
                String[] mealTypeOrder = {"BREAKFAST", "LUNCH", "DINNER", "SNACK"};
                
                for (String mealType : mealTypeOrder) {
                    List<LoggedProduct> mealProducts = productsByMealType.get(mealType);
                    if (mealProducts != null && !mealProducts.isEmpty()) {
                        document.add(new Paragraph(mealType + ":", headerFont));
                        document.add(new Paragraph(" "));
                        
                        PdfPTable mealTable = new PdfPTable(9);
                        mealTable.setWidthPercentage(100);
                        
                        addTableHeader(mealTable, "Product", "EAN13", "Quantity", "Calories", "Carbs", "Protein", "Fat", "Meal Type", "Logged");
                        for (LoggedProduct loggedProduct : mealProducts) {
                            Product product = productMap.get(loggedProduct.getEan13Code());
                            if (product != null) {
                                try {
                                    double quantity = loggedProduct.getQuantity();
                                    NutritionInfo nutrition = product.getNutritionInfo();
                                    double calories = (nutrition != null && nutrition.getCalories() != null ? nutrition.getCalories() : 0) * quantity / 100;
                                    double carbs = (nutrition != null && nutrition.getCarbs() != null ? nutrition.getCarbs() : 0) * quantity / 100;
                                    double protein = (nutrition != null && nutrition.getProtein() != null ? nutrition.getProtein() : 0) * quantity / 100;
                                    double fat = (nutrition != null && nutrition.getFat() != null ? nutrition.getFat() : 0) * quantity / 100;
                                    
                                    mealTable.addCell(new PdfPCell(new Phrase(product.getProductName() != null ? product.getProductName() : "N/A", normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase(loggedProduct.getEan13Code() != null ? loggedProduct.getEan13Code() : "N/A", normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase(String.format("%.1f %s", quantity, loggedProduct.getUnit() != null ? loggedProduct.getUnit() : "g"), normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase(String.format("%.1f", calories), normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase(String.format("%.1f", carbs), normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase(String.format("%.1f", protein), normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase(String.format("%.1f", fat), normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase(loggedProduct.getMealType() != null ? loggedProduct.getMealType().getValue() : "N/A", normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase(formatDateTime(loggedProduct.getLogDate()), normalFont)));
                                } catch (Exception e) {
                                    System.err.println("Error processing product in table: " + e.getMessage());
                                    mealTable.addCell(new PdfPCell(new Phrase("Error loading product", normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase(loggedProduct.getEan13Code() != null ? loggedProduct.getEan13Code() : "N/A", normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase("N/A", normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase("N/A", normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase("N/A", normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase("N/A", normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase("N/A", normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase("N/A", normalFont)));
                                    mealTable.addCell(new PdfPCell(new Phrase("N/A", normalFont)));
                                }
                            } else {
                                mealTable.addCell(new PdfPCell(new Phrase("Product not found", normalFont)));
                                mealTable.addCell(new PdfPCell(new Phrase(loggedProduct.getEan13Code() != null ? loggedProduct.getEan13Code() : "N/A", normalFont)));
                                mealTable.addCell(new PdfPCell(new Phrase(String.format("%.1f %s", loggedProduct.getQuantity(), loggedProduct.getUnit() != null ? loggedProduct.getUnit() : "g"), normalFont)));
                                mealTable.addCell(new PdfPCell(new Phrase("N/A", normalFont)));
                                mealTable.addCell(new PdfPCell(new Phrase("N/A", normalFont)));
                                mealTable.addCell(new PdfPCell(new Phrase("N/A", normalFont)));
                                mealTable.addCell(new PdfPCell(new Phrase("N/A", normalFont)));
                                mealTable.addCell(new PdfPCell(new Phrase(loggedProduct.getMealType() != null ? loggedProduct.getMealType().getValue() : "N/A", normalFont)));
                                mealTable.addCell(new PdfPCell(new Phrase(formatDateTime(loggedProduct.getLogDate()), normalFont)));
                            }
                        }
                        
                        document.add(mealTable);
                        document.add(new Paragraph(" "));
                    }
                }
            }
            
            document.close();
            
        } catch (Exception e) {
            throw new IOException("Failed to generate PDF report: " + e.getMessage(), e);
        }
        
        return outputStream.toByteArray();
    }
    
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private void addTableHeader(PdfPTable table, String... headers) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
            table.addCell(cell);
        }
    }
    
    private void addProgressRow(PdfPTable table, String nutrient, String current, String goal, String progress) {
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        table.addCell(new PdfPCell(new Phrase(nutrient, normalFont)));
        table.addCell(new PdfPCell(new Phrase(current, normalFont)));
        table.addCell(new PdfPCell(new Phrase(goal, normalFont)));
        table.addCell(new PdfPCell(new Phrase(progress, normalFont)));
    }
    
    private Map<String, List<LoggedProduct>> groupProductsByMealType(List<LoggedProduct> loggedProducts) {
        Map<String, List<LoggedProduct>> groupedProducts = new HashMap<>();
        
        for (LoggedProduct loggedProduct : loggedProducts) {
            try {
                String mealType = loggedProduct.getMealType() != null ? 
                    loggedProduct.getMealType().getValue().toUpperCase() : "UNKNOWN";
                groupedProducts.computeIfAbsent(mealType, k -> new java.util.ArrayList<>()).add(loggedProduct);
            } catch (Exception e) {
                System.err.println("Error grouping product by meal type: " + e.getMessage());
            }
        }
        
        return groupedProducts;
    }
}