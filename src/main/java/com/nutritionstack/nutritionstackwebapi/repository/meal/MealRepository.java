package com.nutritionstack.nutritionstackwebapi.repository.meal;

import com.nutritionstack.nutritionstackwebapi.model.meal.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    
    List<Meal> findByCreatedByOrderByCreatedAtDesc(Long createdBy);
    
    @Query("SELECT m FROM Meal m WHERE m.createdBy = :userId OR :isAdmin = true")
    List<Meal> findByCreatedByOrAdmin(@Param("userId") Long userId, @Param("isAdmin") boolean isAdmin);
    
    @Query("SELECT m FROM Meal m WHERE m.id = :id AND (m.createdBy = :userId OR :isAdmin = true)")
    Optional<Meal> findByIdAndCreatedByOrAdmin(@Param("id") Long id, @Param("userId") Long userId, @Param("isAdmin") boolean isAdmin);
    
    boolean existsByIdAndCreatedBy(Long id, Long createdBy);
}
