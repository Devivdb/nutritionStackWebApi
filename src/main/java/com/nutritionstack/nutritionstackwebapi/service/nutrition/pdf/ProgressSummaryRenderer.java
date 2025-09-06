package com.nutritionstack.nutritionstackwebapi.service.nutrition.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.UserGoal;
import com.nutritionstack.nutritionstackwebapi.service.nutrition.NutritionAggregationService;
import org.springframework.stereotype.Component;

@Component
public class ProgressSummaryRenderer {
    
    private final PdfTableBuilder tableBuilder;
    private final PdfDocumentBuilder documentBuilder;
    
    public ProgressSummaryRenderer(PdfTableBuilder tableBuilder, PdfDocumentBuilder documentBuilder) {
        this.tableBuilder = tableBuilder;
        this.documentBuilder = documentBuilder;
    }
    
    public void renderProgressSummary(Document document, UserGoal userGoal, 
                                    NutritionAggregationService.NutritionSummary nutritionSummary,
                                    NutritionAggregationService.ProgressSummary progressSummary) throws DocumentException {
        if (userGoal == null || nutritionSummary == null || progressSummary == null) {
            return;
        }
        
        documentBuilder.addHeader(document, "Progress Summary");
        documentBuilder.addSpacing(document);
        
        PdfPTable progressTable = tableBuilder.createTable(4);
        tableBuilder.addTableHeader(progressTable, documentBuilder.getHeaderFont(), 
            "Nutrient", "Current", "Goal", "Progress");
        
        addProgressRow(progressTable, "Calories", 
            String.format("%.1f kcal", nutritionSummary.getTotalNutrition().getCalories()),
            String.format("%.1f kcal", userGoal.getCaloriesGoal()),
            String.format("%.1f%%", progressSummary.getCaloriesProgress()));
            
        addProgressRow(progressTable, "Protein", 
            String.format("%.1f g", nutritionSummary.getTotalNutrition().getProtein()),
            String.format("%.1f g", userGoal.getProteinGoal()),
            String.format("%.1f%%", progressSummary.getProteinProgress()));
            
        addProgressRow(progressTable, "Carbs", 
            String.format("%.1f g", nutritionSummary.getTotalNutrition().getCarbs()),
            String.format("%.1f g", userGoal.getCarbsGoal()),
            String.format("%.1f%%", progressSummary.getCarbsProgress()));
            
        addProgressRow(progressTable, "Fat", 
            String.format("%.1f g", nutritionSummary.getTotalNutrition().getFat()),
            String.format("%.1f g", userGoal.getFatGoal()),
            String.format("%.1f%%", progressSummary.getFatProgress()));
        
        document.add(progressTable);
        documentBuilder.addSpacing(document);
    }
    
    private void addProgressRow(PdfPTable table, String nutrient, String current, String goal, String progress) {
        tableBuilder.addProgressRow(table, documentBuilder.getSmallFont(), nutrient, current, goal, progress);
    }
}
