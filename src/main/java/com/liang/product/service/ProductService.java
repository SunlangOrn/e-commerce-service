package com.liang.product.service;

import com.liang.product.dto.ProductRequest;
import com.liang.product.dto.ProductResponse;
import java.util.List;

import com.liang.product.dto.ProductResponseDetail;
import org.springframework.data.domain.Page;

public interface ProductService {
    // Public catalog - active products only.
    Page<ProductResponse> browse(Integer page, Integer size, Long categoryId, String keyword);

    ProductResponse view(Long id);

    Page<ProductResponseDetail> listAll(Integer page, Integer size);

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

}
