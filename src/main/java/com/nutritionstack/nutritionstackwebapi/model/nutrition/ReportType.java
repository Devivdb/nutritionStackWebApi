package com.nutritionstack.nutritionstackwebapi.model.nutrition;

public enum ReportType {
    DAY("Day Report"),
    WEEK("Week Report"), 
    MONTHLY("Monthly Report"),
    CUSTOM("Custom Report");
    
    private final String displayName;
    
    ReportType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static ReportType fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return ReportType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
