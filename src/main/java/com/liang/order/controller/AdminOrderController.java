package com.liang.order.controller;

import com.liang.order.dto.OrderRequestUpdate;
import com.liang.order.dto.OrderResponseDetail;
import com.liang.order.service.OrderService;
import com.liang.shared.entity.HttpBodyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.liang.shared.api.ControllerHandler.responseSucceed;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/v1/orders")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<HttpBodyResponse<Page<OrderResponseDetail>>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String orderStatus,
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return responseSucceed(orderService.adminList(userId, orderStatus, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpBodyResponse<OrderResponseDetail>> view(@PathVariable Long id) {
        return responseSucceed(orderService.adminView(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<HttpBodyResponse<OrderResponseDetail>> updateStatus(
            @PathVariable Long id,
            @RequestBody OrderRequestUpdate request
    ) {
        return responseSucceed(orderService.adminUpdateStatus(id, request));
    }

}
