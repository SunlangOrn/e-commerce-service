package com.liang.product.service;

import com.liang.product.dto.ProductRequest;
import com.liang.product.dto.ProductResponse;
import java.util.List;

import com.liang.product.dto.ProductResponseDetail;
import org.springframework.data.domain.Page;

public interface ProductService {
    // Public catalog - active products only.
    Page<ProductResponse> browse(Integer page, Integer size, Long categoryId, String keyword);

    ProductResponse viewPublic(Long id);

    Page<ProductResponseDetail> listAll(Integer page, Integer size);

    ProductResponseDetail view(Long id);

    ProductResponseDetail create(ProductRequest request);

    ProductResponseDetail update(Long id, ProductRequest request);

    void delete(Long id);

    void active(Long id);

    void inactive(Long id);

}
