package com.nutritionstack.nutritionstackwebapi.util.validation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DateValidationRule {
    boolean isValid(LocalDate date, LocalDateTime startDate, LocalDateTime endDate);
    String getErrorMessage();
}
