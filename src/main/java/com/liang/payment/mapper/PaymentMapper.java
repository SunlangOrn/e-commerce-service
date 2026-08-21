package com.liang.payment.mapper;

import com.liang.payment.dto.PaymentResponse;
import com.liang.payment.entity.Payment;

public class PaymentMapper {
    private PaymentMapper() {}

    public static PaymentResponse toResponse(Payment payment) {
        var order = payment.getOrder();
        var user = order != null ? order.getUser() : null;

        return new PaymentResponse(
                payment.getId(),
                order != null ? order.getId() : null,
                order != null ? order.getOrderNumber() : null,
                user != null ? user.getId() : null,
                user != null ? user.getEmail() : null,
                payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null,
                payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : null,
                payment.getAmount(),
                payment.getCurrency(),
                payment.getTransactionReference(),
                payment.getPaidAt(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}