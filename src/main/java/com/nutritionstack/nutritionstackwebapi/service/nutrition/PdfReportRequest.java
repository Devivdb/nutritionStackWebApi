package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.UserGoal;
import com.nutritionstack.nutritionstackwebapi.model.tracking.LoggedProduct;

import java.time.LocalDateTime;
import java.util.List;

public class PdfReportRequest {
    private final User user;
    private final UserGoal userGoal;
    private final NutritionAggregationService.NutritionSummary nutritionSummary;
    private final NutritionAggregationService.ProgressSummary progressSummary;
    private final List<LoggedProduct> loggedProducts;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String reportType;
    private final Long reportId;
    
    private PdfReportRequest(Builder builder) {
        this.user = builder.user;
        this.userGoal = builder.userGoal;
        this.nutritionSummary = builder.nutritionSummary;
        this.progressSummary = builder.progressSummary;
        this.loggedProducts = builder.loggedProducts;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.reportType = builder.reportType;
        this.reportId = builder.reportId;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public User getUser() { return user; }
    public UserGoal getUserGoal() { return userGoal; }
    public NutritionAggregationService.NutritionSummary getNutritionSummary() { return nutritionSummary; }
    public NutritionAggregationService.ProgressSummary getProgressSummary() { return progressSummary; }
    public List<LoggedProduct> getLoggedProducts() { return loggedProducts; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public String getReportType() { return reportType; }
    public Long getReportId() { return reportId; }
    
    public static class Builder {
        private User user;
        private UserGoal userGoal;
        private NutritionAggregationService.NutritionSummary nutritionSummary;
        private NutritionAggregationService.ProgressSummary progressSummary;
        private List<LoggedProduct> loggedProducts;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String reportType;
        private Long reportId;
        
        public Builder user(User user) {
            this.user = user;
            return this;
        }
        
        public Builder userGoal(UserGoal userGoal) {
            this.userGoal = userGoal;
            return this;
        }
        
        public Builder nutritionSummary(NutritionAggregationService.NutritionSummary nutritionSummary) {
            this.nutritionSummary = nutritionSummary;
            return this;
        }
        
        public Builder progressSummary(NutritionAggregationService.ProgressSummary progressSummary) {
            this.progressSummary = progressSummary;
            return this;
        }
        
        public Builder loggedProducts(List<LoggedProduct> loggedProducts) {
            this.loggedProducts = loggedProducts;
            return this;
        }
        
        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }
        
        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }
        
        public Builder reportType(String reportType) {
            this.reportType = reportType;
            return this;
        }
        
        public Builder reportId(Long reportId) {
            this.reportId = reportId;
            return this;
        }
        
        public PdfReportRequest build() {
            return new PdfReportRequest(this);
        }
    }
}
