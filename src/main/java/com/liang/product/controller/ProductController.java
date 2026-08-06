package com.liang.product.controller;

import com.liang.product.dto.ProductRequest;
import com.liang.product.dto.ProductResponse;
import com.liang.product.dto.ProductResponseDetail;
import com.liang.product.service.ProductService;
import com.liang.shared.entity.HttpBodyPagingResponse;
import com.liang.shared.entity.HttpBodyResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.liang.shared.api.ControllerHandler.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<HttpBodyResponse<List<ProductResponseDetail>>> listAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        Page<ProductResponseDetail> products = productService.listAll(page, size);

        return responsePaging(
                products.getContent(),
                HttpBodyPagingResponse.of(
                        products.getNumber(),
                        products.getSize(),
                        products.getTotalElements(),
                        products.getTotalPages()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpBodyResponse<ProductResponseDetail>> view(@PathVariable Long id) {
        return responseSucceed(productService.view(id));
    }

    @PostMapping
    public ResponseEntity<HttpBodyResponse<ProductResponseDetail>> create(
            @Valid @RequestBody ProductRequest request) {
        return responseCreated(productService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpBodyResponse<ProductResponseDetail>> update(
            @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return responseSucceed(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return responseDeleted();
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> active(@PathVariable Long id) {
        productService.active(id);
        return responseOK();
    }

    @PatchMapping("/{id}/inactive")
    public ResponseEntity<Void> inactive(@PathVariable Long id) {
        productService.inactive(id);
        return responseOK();
    }

}
