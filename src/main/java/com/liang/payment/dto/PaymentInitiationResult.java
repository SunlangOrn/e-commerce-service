package com.liang.payment.dto;

import com.liang.payment.entity.Payment;

public record PaymentInitiationResult(
        Payment payment,
        AbaPayWayResponse abaPayWayResponse
) {
}
