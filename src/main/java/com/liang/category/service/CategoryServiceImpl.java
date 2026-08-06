package com.liang.category.service;

import com.liang.category.dto.CategoryRequest;
import com.liang.category.dto.CategoryResponse;
import com.liang.category.dto.CategoryResponseDetail;
import com.liang.category.dto.CategoryUpdateRequest;
import com.liang.category.entity.Category;
import com.liang.category.entity.Status;
import com.liang.category.mapper.CategoryMapper;
import com.liang.category.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> publicList() {
        return categoryRepository.findByStatus(Status.ACTIVE).stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse publicView(Long id) {
        Category category = categoryRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

    @Override
    public List<CategoryResponseDetail> listAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponseDetail)
                .toList();
    }

    @Override
    public CategoryResponseDetail view(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        return categoryMapper.toResponseDetail(category);
    }

    @Override
    @Transactional
    public CategoryResponseDetail create(CategoryRequest request) {
        Category category = categoryMapper.from(request);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDetail(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDetail update(Long id, CategoryUpdateRequest updateRequest) {
        Category oldData = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        if (updateRequest.getName() != null && !updateRequest.getName().isBlank()) {
            oldData.setName(updateRequest.getName());
        }

        if (updateRequest.getStatus() != null && !updateRequest.getStatus().isBlank()) {
            try {
                oldData.setStatus(Status.valueOf(updateRequest.getStatus().toUpperCase().trim()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + updateRequest.getStatus());
            }
        }

        Category updatedCategory = categoryRepository.save(oldData);
        return categoryMapper.toResponseDetail(updatedCategory);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    @Override
    public void active(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        category.setStatus(Status.ACTIVE);
        categoryRepository.save(category);
    }

    @Override
    public void inactive(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        category.setStatus(Status.INACTIVE);
        categoryRepository.save(category);
    }
}