package com.nutritionstack.nutritionstackwebapi.util.validation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateValidationRules {
    
    public static final DateValidationRule NOT_NULL_DATE = new DateValidationRule() {
        @Override
        public boolean isValid(LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
            return date != null;
        }
        
        @Override
        public String getErrorMessage() {
            return "Date is required";
        }
    };
    
    public static final DateValidationRule NOT_NULL_START_DATE = new DateValidationRule() {
        @Override
        public boolean isValid(LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
            return startDate != null;
        }
        
        @Override
        public String getErrorMessage() {
            return "Start date is required";
        }
    };
    
    public static final DateValidationRule NOT_NULL_END_DATE = new DateValidationRule() {
        @Override
        public boolean isValid(LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
            return endDate != null;
        }
        
        @Override
        public String getErrorMessage() {
            return "End date is required";
        }
    };
    
    public static final DateValidationRule NOT_IN_FUTURE_DATE = new DateValidationRule() {
        @Override
        public boolean isValid(LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
            return date == null || !date.isAfter(LocalDate.now());
        }
        
        @Override
        public String getErrorMessage() {
            return "Date cannot be in the future";
        }
    };
    
    public static final DateValidationRule NOT_IN_FUTURE_START_DATE = new DateValidationRule() {
        @Override
        public boolean isValid(LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
            return startDate == null || !startDate.isAfter(LocalDateTime.now());
        }
        
        @Override
        public String getErrorMessage() {
            return "Start date cannot be in the future";
        }
    };
    
    public static final DateValidationRule NOT_IN_FUTURE_END_DATE = new DateValidationRule() {
        @Override
        public boolean isValid(LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
            return endDate == null || !endDate.isAfter(LocalDateTime.now());
        }
        
        @Override
        public String getErrorMessage() {
            return "End date cannot be in the future";
        }
    };
    
    public static final DateValidationRule START_BEFORE_END = new DateValidationRule() {
        @Override
        public boolean isValid(LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
            return startDate == null || endDate == null || !startDate.isAfter(endDate);
        }
        
        @Override
        public String getErrorMessage() {
            return "Start date must be before or equal to end date";
        }
    };
    
    public static DateValidationRule weekRange(int maxDays) {
        return new DateValidationRule() {
            @Override
            public boolean isValid(LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
                if (startDate == null || endDate == null) return true;
                long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
                return daysBetween >= 0 && daysBetween <= maxDays;
            }
            
            @Override
            public String getErrorMessage() {
                return "Week report must cover 1-" + maxDays + " days";
            }
        };
    }
    
    public static DateValidationRule customRange(int maxDays) {
        return new DateValidationRule() {
            @Override
            public boolean isValid(LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
                if (startDate == null || endDate == null) return true;
                long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
                return daysBetween >= 0 && daysBetween <= maxDays;
            }
            
            @Override
            public String getErrorMessage() {
                return "Custom report cannot cover more than " + maxDays + " days";
            }
        };
    }
}
