package com.liang.product.controller;

import static com.liang.shared.api.ControllerHandler.responsePaging;
import static com.liang.shared.api.ControllerHandler.responseSucceed;

import com.liang.product.dto.ProductResponse;
import com.liang.product.service.ProductService;
import com.liang.shared.entity.HttpBodyPagingResponse;
import com.liang.shared.entity.HttpBodyResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductPublicController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<HttpBodyResponse<List<ProductResponse>>> browse(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        Page<ProductResponse> products = productService.browse(page, size, categoryId, keyword);
        return responsePaging(
                products.getContent(),
                HttpBodyPagingResponse.of(
                        products.getNumber(), products.getSize(), products.getTotalElements(), products.getTotalPages()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpBodyResponse<ProductResponse>> view(@PathVariable Long id) {
        return responseSucceed(productService.view(id));
    }
}
