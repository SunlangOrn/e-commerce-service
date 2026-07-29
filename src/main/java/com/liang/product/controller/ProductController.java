package com.liang.product.controller;

import static com.liang.shared.api.ControllerHandler.responseCreated;
import static com.liang.shared.api.ControllerHandler.responseDeleted;
import static com.liang.shared.api.ControllerHandler.responsePaging;
import static com.liang.shared.api.ControllerHandler.responseSucceed;

import com.liang.product.dto.ProductImageRequest;
import com.liang.product.dto.ProductImageResponse;
import com.liang.product.dto.ProductRequest;
import com.liang.product.dto.ProductResponse;
import com.liang.product.service.ProductService;
import com.liang.shared.entity.HttpBodyPagingResponse;
import com.liang.shared.entity.HttpBodyResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<HttpBodyResponse<List<ProductResponse>>> listAll(
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Page<ProductResponse> products = productService.listAll(page, size);
        return responsePaging(
                products.getContent(),
                HttpBodyPagingResponse.of(
                        products.getNumber(),
                        products.getSize(),
                        products.getTotalElements(),
                        products.getTotalPages()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpBodyResponse<ProductResponse>> view(@PathVariable Long id) {
        return responseSucceed(productService.view(id));
    }

    @PostMapping
    public ResponseEntity<HttpBodyResponse<ProductResponse>> create(
            @Valid @RequestBody ProductRequest request) {
        return responseCreated(productService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpBodyResponse<ProductResponse>> update(
            @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return responseSucceed(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return responseDeleted();
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<HttpBodyResponse<List<ProductImageResponse>>> listImages(@PathVariable Long id) {
        return responseSucceed(productService.listImages(id));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<HttpBodyResponse<ProductImageResponse>> addImage(
            @PathVariable Long id, @Valid @RequestBody ProductImageRequest request) {
        return responseCreated(productService.addImage(id, request));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id, @PathVariable Long imageId) {
        productService.deleteImage(id, imageId);
        return responseDeleted();
    }
}
