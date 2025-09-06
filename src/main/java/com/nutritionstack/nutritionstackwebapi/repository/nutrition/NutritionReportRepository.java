package com.nutritionstack.nutritionstackwebapi.repository.nutrition;

import com.nutritionstack.nutritionstackwebapi.model.nutrition.NutritionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NutritionReportRepository extends JpaRepository<NutritionReport, Long> {
    
    List<NutritionReport> findByUserIdOrderByGeneratedAtDesc(Long userId);
    
    Optional<NutritionReport> findByIdAndUserId(Long reportId, Long userId);
    
    @Query("SELECT nr FROM NutritionReport nr WHERE nr.user.id = :userId AND nr.reportType = :reportType ORDER BY nr.generatedAt DESC")
    List<NutritionReport> findByUserIdAndReportTypeOrderByGeneratedAtDesc(@Param("userId") Long userId, @Param("reportType") String reportType);
    
    boolean existsByIdAndUserId(Long reportId, Long userId);
    
    @Query("SELECT COUNT(nr) FROM NutritionReport nr WHERE nr.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
