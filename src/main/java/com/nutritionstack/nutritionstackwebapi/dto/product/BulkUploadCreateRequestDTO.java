package com.nutritionstack.nutritionstackwebapi.dto.product;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class BulkUploadCreateRequestDTO {
    
    @NotNull(message = "JSON file is required")
    private MultipartFile jsonFile;
    
    public BulkUploadCreateRequestDTO() {}
    
    public BulkUploadCreateRequestDTO(MultipartFile jsonFile) {
        this.jsonFile = jsonFile;
    }
    
    public MultipartFile getJsonFile() {
        return jsonFile;
    }
    
    public void setJsonFile(MultipartFile jsonFile) {
        this.jsonFile = jsonFile;
    }
}
