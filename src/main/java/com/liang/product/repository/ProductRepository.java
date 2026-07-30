package com.liang.product.repository;

import com.liang.category.entity.Status;
import com.liang.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p FROM Product p 
        WHERE p.status = :status 
          AND (:categoryId IS NULL OR p.category.id = :categoryId) 
          AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Product> searchProducts(
            @Param("status") Status status,
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.status = :status")
    Optional<Product> findByIdAndStatus(@Param("id") Long id, @Param("status") Status status);
}