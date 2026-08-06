package com.liang.category.repository;

import com.liang.category.entity.Category;
import com.liang.category.entity.Status;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByStatus(Status status);

    Optional<Category> findByIdAndStatus(Long id, Status status);
}