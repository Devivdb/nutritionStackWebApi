package com.nutritionstack.nutritionstackwebapi.repository.nutrition;

import com.nutritionstack.nutritionstackwebapi.model.nutrition.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {
    
    Optional<UserGoal> findByUserIdAndIsActiveTrue(Long userId);
    
    List<UserGoal> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<UserGoal> findByIdAndUserId(Long id, Long userId);
    
    @Modifying
    @Query("UPDATE UserGoal g SET g.isActive = false WHERE g.user.id = :userId AND g.isActive = true")
    void deactivateAllActiveGoals(@Param("userId") Long userId);
    
    boolean existsByUserIdAndIsActiveTrue(Long userId);
}
