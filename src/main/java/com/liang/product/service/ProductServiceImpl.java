package com.liang.product.service;

import com.liang.shared.api.NotFoundException;
import com.liang.product.dto.ProductImageRequest;
import com.liang.product.dto.ProductImageResponse;
import com.liang.product.dto.ProductRequest;
import com.liang.product.dto.ProductResponse;
import com.liang.product.entity.Product;
import com.liang.product.entity.ProductImage;
import com.liang.product.mapper.ProductMapper;
import com.liang.product.repository.ProductImageRepository;
import com.liang.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponse> browse(Integer page, Integer size, Long categoryId, String keyword) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 20 : size);
        Page<Product> products;
        if (keyword != null && !keyword.isBlank()) {
            products = productRepository.findByIsActiveTrueAndNameContainingIgnoreCase(keyword, pageable);
        } else if (categoryId != null) {
            products = productRepository.findByIsActiveTrueAndCategoryId(categoryId, pageable);
        } else {
            products = productRepository.findByIsActiveTrue(pageable);
        }
        return products.map(productMapper::toResponse);
    }

    @Override
    public ProductResponse view(Long id) {
        return productMapper.toResponse(findVisibleOrThrow(id));
    }

    @Override
    public Page<ProductResponse> listAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 20 : size);
        return productRepository.findAllBy(pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.from(request);
        if (product.getStockQuantity() == null) {
            product.setStockQuantity(0);
        }
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        productMapper.updateFrom(request, product);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        productRepository.delete(findOrThrow(id));
    }

    @Override
    public List<ProductImageResponse> listImages(Long productId) {
        findVisibleOrThrow(productId);
        return productImageRepository.findByProductId(productId).stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductImageResponse addImage(Long productId, ProductImageRequest request) {
        findOrThrow(productId);
        ProductImage image = new ProductImage();
        image.setProductId(productId);
        image.setImageUrl(request.getImageUrl());
        return productMapper.toResponse(productImageRepository.save(image));
    }

    @Override
    @Transactional
    public void deleteImage(Long productId, Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Image not found"));
        if (!image.getProductId().equals(productId)) {
            throw new IllegalArgumentException("Image does not belong to this product");
        }
        productImageRepository.delete(image);
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }
    private Product findVisibleOrThrow(Long id) {
        return productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }
}
