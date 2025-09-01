package com.nutritionstack.nutritionstackwebapi.repository.tracking;

import com.nutritionstack.nutritionstackwebapi.model.tracking.LoggedProduct;
import com.nutritionstack.nutritionstackwebapi.model.meal.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoggedProductRepository extends JpaRepository<LoggedProduct, Long> {

    List<LoggedProduct> findByUserIdOrderByLogDateDesc(Long userId);

    @Query("SELECT lp FROM LoggedProduct lp WHERE lp.userId = :userId AND lp.logDate BETWEEN :startDate AND :endDate ORDER BY lp.logDate DESC")
    List<LoggedProduct> findByUserIdAndLogDateBetweenOrderByLogDateDesc(
        @Param("userId") Long userId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );

    List<LoggedProduct> findByUserIdAndMealTypeOrderByLogDateDesc(Long userId, MealType mealType);

    @Query("SELECT lp FROM LoggedProduct lp WHERE lp.userId = :userId AND DATE(lp.logDate) = DATE(:logDate) ORDER BY lp.logDate DESC")
    List<LoggedProduct> findByUserIdAndLogDateOrderByLogDateDesc(
        @Param("userId") Long userId, 
        @Param("logDate") LocalDateTime logDate
    );

    boolean existsByIdAndUserId(Long id, Long userId);

    Optional<LoggedProduct> findByIdAndUserId(Long id, Long userId);
}
