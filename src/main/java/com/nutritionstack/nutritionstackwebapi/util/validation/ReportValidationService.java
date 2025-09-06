package com.nutritionstack.nutritionstackwebapi.util.validation;

import com.nutritionstack.nutritionstackwebapi.model.nutrition.ReportType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.nutritionstack.nutritionstackwebapi.util.validation.DateValidationRules.*;

@Service
public class ReportValidationService {
    
    public ValidationResult validateReportRequest(String reportType, LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
        List<String> errors = new ArrayList<>();
        
        ReportType type = ReportType.fromString(reportType);
        if (type == null) {
            errors.add("Report type must be DAY, WEEK, MONTHLY, or CUSTOM");
            return new ValidationResult(false, errors);
        }
        
        switch (type) {
            case DAY:
                validateDayReport(date, errors);
                break;
            case WEEK:
                validateWeekReport(startDate, endDate, errors);
                break;
            case MONTHLY:
                validateMonthlyReport(date, errors);
                break;
            case CUSTOM:
                validateCustomReport(startDate, endDate, errors);
                break;
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    private void validateDayReport(LocalDate date, List<String> errors) {
        if (!NOT_NULL_DATE.isValid(date, null, null)) {
            errors.add(NOT_NULL_DATE.getErrorMessage() + " for day report");
            return;
        }
        
        if (!NOT_IN_FUTURE_DATE.isValid(date, null, null)) {
            errors.add(NOT_IN_FUTURE_DATE.getErrorMessage());
        }
    }
    
    private void validateMonthlyReport(LocalDate date, List<String> errors) {
        if (!NOT_NULL_DATE.isValid(date, null, null)) {
            errors.add(NOT_NULL_DATE.getErrorMessage() + " for monthly report");
            return;
        }
        
        if (!NOT_IN_FUTURE_DATE.isValid(date, null, null)) {
            errors.add(NOT_IN_FUTURE_DATE.getErrorMessage());
        }
    }
    
    private void validateWeekReport(LocalDateTime startDate, LocalDateTime endDate, List<String> errors) {
        if (!NOT_NULL_START_DATE.isValid(null, startDate, endDate)) {
            errors.add(NOT_NULL_START_DATE.getErrorMessage() + " for week report");
            return;
        }
        
        if (!NOT_NULL_END_DATE.isValid(null, startDate, endDate)) {
            errors.add(NOT_NULL_END_DATE.getErrorMessage() + " for week report");
            return;
        }
        
        if (!NOT_IN_FUTURE_START_DATE.isValid(null, startDate, endDate)) {
            errors.add(NOT_IN_FUTURE_START_DATE.getErrorMessage());
        }
        
        if (!NOT_IN_FUTURE_END_DATE.isValid(null, startDate, endDate)) {
            errors.add(NOT_IN_FUTURE_END_DATE.getErrorMessage());
        }
        
        if (!START_BEFORE_END.isValid(null, startDate, endDate)) {
            errors.add(START_BEFORE_END.getErrorMessage());
        }
        
        if (!weekRange(7).isValid(null, startDate, endDate)) {
            errors.add(weekRange(7).getErrorMessage());
        }
    }
    
    private void validateCustomReport(LocalDateTime startDate, LocalDateTime endDate, List<String> errors) {
        if (!NOT_NULL_START_DATE.isValid(null, startDate, endDate)) {
            errors.add(NOT_NULL_START_DATE.getErrorMessage() + " for custom report");
            return;
        }
        
        if (!NOT_NULL_END_DATE.isValid(null, startDate, endDate)) {
            errors.add(NOT_NULL_END_DATE.getErrorMessage() + " for custom report");
            return;
        }
        
        if (!NOT_IN_FUTURE_START_DATE.isValid(null, startDate, endDate)) {
            errors.add(NOT_IN_FUTURE_START_DATE.getErrorMessage());
        }
        
        if (!NOT_IN_FUTURE_END_DATE.isValid(null, startDate, endDate)) {
            errors.add(NOT_IN_FUTURE_END_DATE.getErrorMessage());
        }
        
        if (!START_BEFORE_END.isValid(null, startDate, endDate)) {
            errors.add(START_BEFORE_END.getErrorMessage());
        }
        
        if (!customRange(365).isValid(null, startDate, endDate)) {
            errors.add(customRange(365).getErrorMessage());
        }
    }
    
    public LocalDateTime[] calculateDateRange(ReportType reportType, LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (reportType) {
            case DAY:
                return calculateDayRange(date != null ? date : now.toLocalDate());
                
            case WEEK:
                return startDate != null && endDate != null ? 
                    new LocalDateTime[]{startDate, endDate} : 
                    calculateWeekRange(now);
                
            case MONTHLY:
                return calculateMonthlyRange(date != null ? date : now.toLocalDate());
                
            case CUSTOM:
                return new LocalDateTime[]{startDate, endDate};
                
            default:
                throw new IllegalArgumentException("Invalid report type: " + reportType);
        }
    }
    
    private LocalDateTime[] calculateDayRange(LocalDate targetDate) {
        LocalDateTime dayStart = targetDate.atStartOfDay();
        LocalDateTime dayEnd = targetDate.atTime(23, 59, 59);
        return new LocalDateTime[]{dayStart, dayEnd};
    }
    
    private LocalDateTime[] calculateWeekRange(LocalDateTime now) {
        LocalDateTime weekStart = now.toLocalDate()
            .minusDays(now.getDayOfWeek().getValue() - 1)
            .atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        return new LocalDateTime[]{weekStart, weekEnd};
    }
    
    private LocalDateTime[] calculateMonthlyRange(LocalDate targetMonth) {
        LocalDateTime monthStart = targetMonth.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = targetMonth.withDayOfMonth(targetMonth.lengthOfMonth()).atTime(23, 59, 59);
        return new LocalDateTime[]{monthStart, monthEnd};
    }
}
