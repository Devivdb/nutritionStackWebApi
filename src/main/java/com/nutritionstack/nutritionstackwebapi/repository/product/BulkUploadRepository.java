package com.nutritionstack.nutritionstackwebapi.repository.product;

import com.nutritionstack.nutritionstackwebapi.model.product.BulkUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BulkUploadRepository extends JpaRepository<BulkUpload, Long> {
    
    List<BulkUpload> findByUploadedByOrderByUploadedAtDesc(Long uploadedBy);
    
    List<BulkUpload> findAllByOrderByUploadedAtDesc();
}
