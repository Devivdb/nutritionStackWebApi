package com.nutritionstack.nutritionstackwebapi.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BulkUploadValidationErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private BulkUploadValidationDetails details;
    
    public BulkUploadValidationErrorResponse() {}
    
    public BulkUploadValidationErrorResponse(LocalDateTime timestamp, int status, String error, 
                                           String message, BulkUploadValidationDetails details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }
    
    // Getters and setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public BulkUploadValidationDetails getDetails() { return details; }
    public void setDetails(BulkUploadValidationDetails details) { this.details = details; }
    
    public static class BulkUploadValidationDetails {
        private int totalProducts;
        private int validProducts;
        private int invalidProducts;
        private List<ProductValidationError> errors;
        
        public BulkUploadValidationDetails() {}
        
        public BulkUploadValidationDetails(int totalProducts, int validProducts, int invalidProducts, 
                                        List<ProductValidationError> errors) {
            this.totalProducts = totalProducts;
            this.validProducts = validProducts;
            this.invalidProducts = invalidProducts;
            this.errors = errors;
        }
        
        // Getters and setters
        public int getTotalProducts() { return totalProducts; }
        public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }
        
        public int getValidProducts() { return validProducts; }
        public void setValidProducts(int validProducts) { this.validProducts = validProducts; }
        
        public int getInvalidProducts() { return invalidProducts; }
        public void setInvalidProducts(int invalidProducts) { this.invalidProducts = invalidProducts; }
        
        public List<ProductValidationError> getErrors() { return errors; }
        public void setErrors(List<ProductValidationError> errors) { this.errors = errors; }
    }
    
    public static class ProductValidationError {
        private int index;
        private String ean13Code;
        private String field;
        private String error;
        private String suggestion;
        
        public ProductValidationError() {}
        
        public ProductValidationError(int index, String ean13Code, String field, String error, String suggestion) {
            this.index = index;
            this.ean13Code = ean13Code;
            this.field = field;
            this.error = error;
            this.suggestion = suggestion;
        }
        
        // Getters and setters
        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        
        public String getEan13Code() { return ean13Code; }
        public void setEan13Code(String ean13Code) { this.ean13Code = ean13Code; }
        
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getSuggestion() { return suggestion; }
        public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    }
}
