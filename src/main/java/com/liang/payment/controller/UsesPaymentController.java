package com.liang.payment.controller;

import com.liang.payment.dto.AbaPayWayResponse;
import com.liang.payment.service.PaymentService;
import com.liang.shared.entity.HttpBodyResponse;
import com.liang.shared.metadata.Metadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.liang.shared.api.ControllerHandler.responseSucceed;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class UsesPaymentController {

    private final PaymentService paymentService;

    @PostMapping("/orders/{orderId}/aba-khqr")
    public ResponseEntity<HttpBodyResponse<AbaPayWayResponse>> createAbaKHQR(@PathVariable Long orderId) {
        return responseSucceed(paymentService.createAbaKHQR(new Metadata(), orderId));
    }

    @GetMapping("/orders/{orderId}/aba-khqr/status")
    public ResponseEntity<HttpBodyResponse<AbaPayWayResponse>> checkAbaKHQR(@PathVariable Long orderId) {
        return responseSucceed(paymentService.checkAbaKHQR(new Metadata(), orderId));
    }
}
