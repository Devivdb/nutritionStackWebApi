package com.nutritionstack.nutritionstackwebapi.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class GenericResponseBuilder {
    
    private GenericResponseBuilder() {}
    
    public static <T> ResponseEntity<T> success(T body) {
        return ResponseEntity.ok(body);
    }
    
    public static <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
    
    public static <T> ResponseEntity<T> noContent() {
        return ResponseEntity.noContent().build();
    }
}
