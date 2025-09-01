package com.nutritionstack.nutritionstackwebapi.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class BulkUploadDataDTO {
    
    @NotEmpty(message = "Products list cannot be empty")
    @Size(max = 1000, message = "Cannot upload more than 1000 products at once")
    @Valid
    private List<ProductCreateRequestDTO> products;
    
    public BulkUploadDataDTO() {}
    
    public BulkUploadDataDTO(List<ProductCreateRequestDTO> products) {
        this.products = products;
    }
    
    public List<ProductCreateRequestDTO> getProducts() {
        return products;
    }
    
    public void setProducts(List<ProductCreateRequestDTO> products) {
        this.products = products;
    }
}
