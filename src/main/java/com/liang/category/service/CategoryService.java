package com.liang.category.service;

import com.liang.category.dto.CategoryRequest;
import com.liang.category.dto.CategoryResponse;
import com.liang.category.dto.CategoryResponseDetail;
import com.liang.category.dto.CategoryUpdateRequest;

import java.util.List;

public interface CategoryService {

    List<CategoryResponseDetail> listAll();

    CategoryResponseDetail view(Long id);

    CategoryResponseDetail create(CategoryRequest request);

    CategoryResponseDetail update(Long id, CategoryUpdateRequest updateRequest);

    void delete(Long id);

    void active(Long id);

    void inactive(Long id);

    List<CategoryResponse> publicList();

    CategoryResponse publicView(Long id);
}