package com.liang.product.repository;

import com.liang.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByIsActiveTrueAndNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findByIsActiveTrueAndCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findAllBy(Pageable pageable);

    java.util.Optional<Product> findByIdAndIsActiveTrue(Long id);
}
