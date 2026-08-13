package com.liang.product.service;

import com.liang.category.entity.Category;
import com.liang.category.entity.Status;
import com.liang.category.repository.CategoryRepository;
import com.liang.product.dto.ProductRequest;
import com.liang.product.dto.ProductResponse;
import com.liang.product.dto.ProductResponseDetail;
import com.liang.product.entity.Product;
import com.liang.product.entity.ProductStatus;
import com.liang.product.mapper.ProductMapper;
import com.liang.product.repository.ProductRepository;
import com.liang.shared.api.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponse> browse(Integer page, Integer size, Long categoryId, String keyword) {
        int pageNumber = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size <= 0) ? 20 : size;
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        Page<Product> products = productRepository.searchProducts(
                ProductStatus.ACTIVE,
                categoryId,
                cleanKeyword,
                pageable
        );

        return products.map(productMapper::toResponse);
    }

    @Override
    public ProductResponse viewPublic(Long id) {
        return productMapper.toResponse(findVisibleOrThrow(id));
    }

    @Override
    public Page<ProductResponseDetail> listAll(Integer page, Integer size) {
        int pageNumber = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size <= 0) ? 20 : size;
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.DESC, "id"));
        return productRepository.findAll(pageable).map(productMapper::toDetailResponse);
    }

    @Override
    public ProductResponseDetail view(Long id) {
        return productMapper.toDetailResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public ProductResponseDetail create(ProductRequest request) {
        Product product = productMapper.from(request);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        if (product.getStockQuantity() == null) {
            product.setStockQuantity(0);
        }
        if (product.getStatus() == null) {
            product.setStatus(ProductStatus.ACTIVE);
        }

        return productMapper.toDetailResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponseDetail update(Long id, ProductRequest request) {
        Product product = findOrThrow(id);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        productMapper.updateFrom(request, product);
        return productMapper.toDetailResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findOrThrow(id);
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public void active(Long id) {
        Product product = findOrThrow(id);
        product.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void inactive(Long id) {
        Product product = findOrThrow(id);
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    //for admin
    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    //for user
    private Product findVisibleOrThrow(Long id) {
        return productRepository.findByIdAndStatus(id, ProductStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Product not found or inactive with id: " + id));
    }
}
