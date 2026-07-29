package com.liang.product.service;

import com.liang.product.dto.ProductImageRequest;
import com.liang.product.dto.ProductImageResponse;
import com.liang.product.dto.ProductRequest;
import com.liang.product.dto.ProductResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ProductService {
    // Public catalog - active products only.
    Page<ProductResponse> browse(Integer page, Integer size, Long categoryId, String keyword);

    ProductResponse view(Long id);

    // Admin management - includes inactive products.
    Page<ProductResponse> listAll(Integer page, Integer size);

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

    List<ProductImageResponse> listImages(Long productId);

    ProductImageResponse addImage(Long productId, ProductImageRequest request);

    void deleteImage(Long productId, Long imageId);
}
