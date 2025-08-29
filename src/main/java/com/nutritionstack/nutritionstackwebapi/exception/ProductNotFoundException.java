package com.nutritionstack.nutritionstackwebapi.exception;

public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(String ean13Code) {
        super(String.format("Product not found with EAN13 code: %s", ean13Code));
    }
    
    public ProductNotFoundException(Long id) {
        super(String.format("Product not found with ID: %d", id));
    }
}
