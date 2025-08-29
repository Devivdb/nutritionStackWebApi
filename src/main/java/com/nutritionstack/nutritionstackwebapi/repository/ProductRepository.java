package com.nutritionstack.nutritionstackwebapi.repository;

import com.nutritionstack.nutritionstackwebapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByEan13Code(String ean13Code);
    boolean existsByEan13Code(String ean13Code);
}
