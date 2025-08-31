package com.nutritionstack.nutritionstackwebapi.repository;

import com.nutritionstack.nutritionstackwebapi.model.LoggedProduct;
import com.nutritionstack.nutritionstackwebapi.model.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoggedProductRepository extends JpaRepository<LoggedProduct, Long> {
    
    /**
     * Find all logged products for a specific user
     */
    List<LoggedProduct> findByUserIdOrderByLogDateDesc(Long userId);
    
    /**
     * Find logged products for a specific user within a date range
     */
    @Query("SELECT lp FROM LoggedProduct lp WHERE lp.userId = :userId AND lp.logDate BETWEEN :startDate AND :endDate ORDER BY lp.logDate DESC")
    List<LoggedProduct> findByUserIdAndLogDateBetweenOrderByLogDateDesc(
        @Param("userId") Long userId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Find logged products for a specific user and meal type
     */
    List<LoggedProduct> findByUserIdAndMealTypeOrderByLogDateDesc(Long userId, MealType mealType);
    
    /**
     * Find logged products for a specific user on a specific date
     */
    @Query("SELECT lp FROM LoggedProduct lp WHERE lp.userId = :userId AND DATE(lp.logDate) = DATE(:logDate) ORDER BY lp.logDate DESC")
    List<LoggedProduct> findByUserIdAndLogDateOrderByLogDateDesc(
        @Param("userId") Long userId, 
        @Param("logDate") LocalDateTime logDate
    );
    
    /**
     * Check if a logged product exists for a user
     */
    boolean existsByIdAndUserId(Long id, Long userId);
    
    /**
     * Find logged product by ID and user ID (for security)
     */
    Optional<LoggedProduct> findByIdAndUserId(Long id, Long userId);
}
