package com.nutritionstack.nutritionstackwebapi.dto;

public class ProductInfoDTO {
    
    private String ean13Code;
    private String productName;
    private Double amount;
    private String unit;
    
    public ProductInfoDTO() {}
    
    public ProductInfoDTO(String ean13Code, String productName, Double amount, String unit) {
        this.ean13Code = ean13Code;
        this.productName = productName;
        this.amount = amount;
        this.unit = unit;
    }
    
    // Getters and Setters
    public String getEan13Code() { return ean13Code; }
    public void setEan13Code(String ean13Code) { this.ean13Code = ean13Code; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
