package com.liang.order.controller;

import static com.liang.shared.api.ControllerHandler.responseCreated;
import static com.liang.shared.api.ControllerHandler.responseSucceed;

import com.liang.order.dto.OrderRequest;
import com.liang.order.dto.OrderResponse;
import com.liang.order.service.OrderService;
import com.liang.shared.entity.HttpBodyResponse;
import com.liang.shared.metadata.Metadata;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class UserOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<HttpBodyResponse<Page<OrderResponse>>> list(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return responseSucceed(orderService.list(new Metadata(), pageable));
    }

    @PostMapping("/checkout")
    public ResponseEntity<HttpBodyResponse<OrderResponse>> checkout(
            @Valid @RequestBody OrderRequest request
    ) {
        return responseCreated(orderService.checkout(new Metadata(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpBodyResponse<OrderResponse>> view(@PathVariable Long id) {
        return responseSucceed(orderService.view(new Metadata(), id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<HttpBodyResponse<OrderResponse>> cancel(@PathVariable Long id) {
        return responseSucceed(orderService.cancel(new Metadata(), id));
    }
}