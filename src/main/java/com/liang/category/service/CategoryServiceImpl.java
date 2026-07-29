package com.liang.category.service;

import com.liang.category.dto.CategoryResponse;
import com.liang.shared.api.NotFoundException;
import com.liang.category.dto.CategoryRequest;
import com.liang.category.dto.CategoryResponseDetail;
import com.liang.category.entity.Category;
import com.liang.category.mapper.CategoryMapper;
import com.liang.category.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Override
  public List<CategoryResponse> publicList(Long parentId) {
    List<Category> categories = parentId == null
        ? categoryRepository.findByParentIdIsNullAndIsActiveTrue()
        : categoryRepository.findByParentIdAndIsActiveTrue(parentId);
    return categories.stream().map(categoryMapper::toResponse).toList();
  }

  @Override
  public CategoryResponse publicView(Long id) {
      return categoryMapper.toResponse(findVisibleOrThrow(id));
  }

  @Override
  public List<CategoryResponseDetail> listAll(Long parentId) {
    List<Category> categories = parentId == null
        ? categoryRepository.findByParentIdIsNull()
        : categoryRepository.findByParentId(parentId);
    return categories.stream().map(categoryMapper::toResponseDetail).toList();
  }

  @Override
  public CategoryResponseDetail view(Long id) {
    return categoryMapper.toResponseDetail(findVisibleOrThrow(id));
  }

  @Override
  @Transactional
  public CategoryResponseDetail create(CategoryRequest request) {
    if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
      throw new IllegalArgumentException("Category name already exists");
    }
    if (request.getParentId() != null) {
      findOrThrow(request.getParentId());
    }
    Category category = categoryMapper.from(request);
    if (category.getIsActive() == null) {
      category.setIsActive(true);
    }
    return categoryMapper.toResponseDetail(categoryRepository.save(category));
  }

  @Override
  @Transactional
  public CategoryResponseDetail update(Long id, CategoryRequest request) {
    Category category = findOrThrow(id);
    if (request.getParentId() != null) {
      if (request.getParentId().equals(id)) {
        throw new IllegalArgumentException("A category cannot be its own parent");
      }
      findOrThrow(request.getParentId());
    }
    categoryMapper.updateFrom(request, category);
    return categoryMapper.toResponseDetail(categoryRepository.save(category));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Category category = findOrThrow(id);
    if (!categoryRepository.findByParentId(id).isEmpty()) {
      throw new IllegalStateException("Cannot delete a category that has subcategories");
    }
    categoryRepository.delete(category);
  }

  private Category findOrThrow(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Category not found"));
  }

  private Category findVisibleOrThrow(Long id) {
    return categoryRepository.findByIdAndIsActiveTrue(id)
        .orElseThrow(() -> new NotFoundException("Category not found"));
  }
}
