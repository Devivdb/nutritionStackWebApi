package com.nutritionstack.nutritionstackwebapi.util;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ReportValidationUtil {
    
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
    }
    
    public static ValidationResult validateReportRequest(String reportType, LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
        List<String> errors = new ArrayList<>();
        
        if (reportType == null || reportType.trim().isEmpty()) {
            errors.add("Report type is required");
        } else if (!isValidReportType(reportType)) {
            errors.add("Report type must be DAY, WEEK, MONTHLY, or CUSTOM");
        }
        if (reportType != null) {
            switch (reportType.toUpperCase()) {
                case "DAY":
                    validateDayReport(date, errors);
                    break;
                case "WEEK":
                    validateWeekReport(startDate, endDate, errors);
                    break;
                case "MONTHLY":
                    validateMonthlyReport(date, errors);
                    break;
                case "CUSTOM":
                    validateCustomReport(startDate, endDate, errors);
                    break;
            }
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    private static boolean isValidReportType(String reportType) {
        return "DAY".equalsIgnoreCase(reportType) ||
               "WEEK".equalsIgnoreCase(reportType) ||
               "MONTHLY".equalsIgnoreCase(reportType) ||
               "CUSTOM".equalsIgnoreCase(reportType);
    }
    
    private static void validateDayReport(LocalDate date, List<String> errors) {
        if (date == null) {
            errors.add("Date is required for day report");
            return;
        }
        
        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) {
            errors.add("Date cannot be in the future");
        }
    }

    private static void validateMonthlyReport(LocalDate date, List<String> errors) {
        if (date == null) {
            errors.add("Date is required for monthly report");
            return;
        }
        
        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) {
            errors.add("Date cannot be in the future");
        }
    }
    
    private static void validateWeekReport(LocalDateTime startDate, LocalDateTime endDate, List<String> errors) {
        if (startDate == null) {
            errors.add("Start date is required for week report");
            return;
        }
        
        if (endDate == null) {
            errors.add("End date is required for week report");
            return;
        }
        
        long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        if (daysBetween < 0 || daysBetween > 7) {
            errors.add("Week report must cover 1-7 days");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (startDate.isAfter(now)) {
            errors.add("Start date cannot be in the future");
        }
        
        if (endDate.isAfter(now)) {
            errors.add("End date cannot be in the future");
        }
        
        if (startDate.isAfter(endDate)) {
            errors.add("Start date must be before or equal to end date");
        }
    }
    
    private static void validateCustomReport(LocalDateTime startDate, LocalDateTime endDate, List<String> errors) {
        if (startDate == null) {
            errors.add("Start date is required for custom report");
            return;
        }
        
        if (endDate == null) {
            errors.add("End date is required for custom report");
            return;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (startDate.isAfter(now)) {
            errors.add("Start date cannot be in the future");
        }
        
        if (endDate.isAfter(now)) {
            errors.add("End date cannot be in the future");
        }
        
        if (startDate.isAfter(endDate)) {
            errors.add("Start date must be before or equal to end date");
        }
        
        long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        if (daysBetween > 365) {
            errors.add("Custom report cannot cover more than 365 days");
        }
        
        if (daysBetween < 0) {
            errors.add("Date range must be positive");
        }
    }
    
    public static LocalDateTime[] calculateDateRange(String reportType, LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (reportType.toUpperCase()) {
            case "DAY":
                LocalDate targetDate = date != null ? date : now.toLocalDate();
                LocalDateTime dayStart = targetDate.atStartOfDay();
                LocalDateTime dayEnd = targetDate.atTime(23, 59, 59);
                return new LocalDateTime[]{dayStart, dayEnd};
                
            case "WEEK":
                if (startDate != null && endDate != null) {
                    return new LocalDateTime[]{startDate, endDate};
                }
                LocalDateTime weekStart = now.toLocalDate()
                    .minusDays(now.getDayOfWeek().getValue() - 1)
                    .atStartOfDay();
                LocalDateTime weekEnd = weekStart.plusDays(6).withHour(23).withMinute(59).withSecond(59);
                return new LocalDateTime[]{weekStart, weekEnd};
                
            case "MONTHLY":
                LocalDate targetMonth = date != null ? date : now.toLocalDate();
                LocalDateTime monthStart = targetMonth.withDayOfMonth(1).atStartOfDay();
                LocalDateTime monthEnd = targetMonth.withDayOfMonth(targetMonth.lengthOfMonth()).atTime(23, 59, 59);
                return new LocalDateTime[]{monthStart, monthEnd};
                
            case "CUSTOM":
                return new LocalDateTime[]{startDate, endDate};
                
            default:
                throw new IllegalArgumentException("Invalid report type: " + reportType);
        }
    }
}
