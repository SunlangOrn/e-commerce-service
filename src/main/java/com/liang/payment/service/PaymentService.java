package com.liang.payment.service;

import com.liang.payment.dto.AbaPayWayResponse;
import com.liang.shared.metadata.Metadata;

public interface PaymentService {

    // Creates ABA KHQR payment request and stores transaction details in DB
    AbaPayWayResponse createAbaKHQR(Metadata metadata, Long orderId);

    // Verifies payment status with ABA server and updates order status in DB
    AbaPayWayResponse checkAbaKHQR(Metadata metadata, Long orderId);
}
