package com.liang.category.service;

import com.liang.category.dto.CategoryRequest;
import com.liang.category.dto.CategoryResponse;
import com.liang.category.dto.CategoryResponseDetail;

import java.util.List;

public interface CategoryService {

  List<CategoryResponseDetail> listAll(Long parentId);

  CategoryResponseDetail view(Long id);

  CategoryResponseDetail create(CategoryRequest request);

  CategoryResponseDetail update(Long id, CategoryRequest request);

  void delete(Long id);

  List<CategoryResponse> publicList(Long parentId);

  CategoryResponse publicView(Long id);
}
