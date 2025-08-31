package com.nutritionstack.nutritionstackwebapi.repository;

import com.nutritionstack.nutritionstackwebapi.model.MealProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealProductRepository extends JpaRepository<MealProduct, Long> {
    
    List<MealProduct> findByMealIdOrderByCreatedAt(Long mealId);
    
    Optional<MealProduct> findByMealIdAndEan13Code(Long mealId, String ean13Code);
    
    @Modifying
    @Query("DELETE FROM MealProduct mp WHERE mp.meal.id = :mealId AND mp.ean13Code = :ean13Code")
    void deleteByMealIdAndEan13Code(@Param("mealId") Long mealId, @Param("ean13Code") String ean13Code);
    
    boolean existsByMealIdAndEan13Code(Long mealId, String ean13Code);
}
