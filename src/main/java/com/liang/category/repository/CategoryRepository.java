package com.liang.category.repository;

import com.liang.category.entity.Category;
import com.liang.category.entity.Status;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<Category> findByParentId(Long parentId);

    List<Category> findByParentIdIsNull();

    // Replaced `IsActiveTrue` with `Status` checks
    List<Category> findByParentIdAndStatus(Long parentId, Status status);

    List<Category> findByParentIdIsNullAndStatus(Status status);

    Optional<Category> findByIdAndStatus(Long id, Status status);
}