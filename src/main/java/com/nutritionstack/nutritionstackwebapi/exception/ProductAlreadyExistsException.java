package com.nutritionstack.nutritionstackwebapi.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    
    public ProductAlreadyExistsException(String ean13Code) {
        super(String.format("Product with EAN13 code '%s' already exists", ean13Code));
    }
}
