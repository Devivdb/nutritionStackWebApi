package com.nutritionstack.nutritionstackwebapi.repository;

import com.nutritionstack.nutritionstackwebapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByEan13Code(String ean13Code);
    boolean existsByEan13Code(String ean13Code);
    
    @Query("SELECT p.ean13Code FROM Product p WHERE p.ean13Code IN :ean13Codes")
    List<String> findEan13CodesByEan13CodeIn(@Param("ean13Codes") List<String> ean13Codes);
    
    @Modifying
    @Query("DELETE FROM Product p WHERE p.bulkUploadId = :bulkUploadId")
    void deleteByBulkUploadId(@Param("bulkUploadId") Long bulkUploadId);
}
