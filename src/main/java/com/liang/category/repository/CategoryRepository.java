package com.liang.category.repository;

import com.liang.category.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  boolean existsByNameIgnoreCase(String name);

  List<Category> findByParentId(Long parentId);

  List<Category> findByParentIdIsNull();

  List<Category> findByParentIdAndIsActiveTrue(Long parentId);

  List<Category> findByParentIdIsNullAndIsActiveTrue();

  Optional<Category> findByIdAndIsActiveTrue(Long id);
}
