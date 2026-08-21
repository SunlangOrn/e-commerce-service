package com.liang.payment.controller;

import static com.liang.shared.api.ControllerHandler.responseSucceed;

import com.liang.payment.dto.PaymentResponse;
import com.liang.payment.service.PaymentService;
import com.liang.shared.entity.HttpBodyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/v1/payments")
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<HttpBodyResponse<Page<PaymentResponse>>> list(
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return responseSucceed(paymentService.adminList(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpBodyResponse<PaymentResponse>> view(@PathVariable Long id) {
        return responseSucceed(paymentService.adminView(id));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<HttpBodyResponse<PaymentResponse>> viewByOrderId(@PathVariable Long orderId) {
        return responseSucceed(paymentService.adminViewByOrderId(orderId));
    }
}