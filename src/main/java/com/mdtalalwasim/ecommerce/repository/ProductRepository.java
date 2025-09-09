package com.mdtalalwasim.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mdtalalwasim.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Existing methods
    List<Product> findByIsActiveTrue();
    List<Product> findByProductCategoryAndIsActiveTrue(String category);

    // ✅ Update Product Name by ID
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.name = :name WHERE p.id = :id")
    int updateProductName(@Param("id") Long id, @Param("name") String name);

    // ✅ Update Price and Active status by ID
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.price = :price, p.isActive = :isActive WHERE p.id = :id")
    int updateProductDetails(@Param("id") Long id, 
                             @Param("price") Double price, 
                             @Param("isActive") Boolean isActive);
}
