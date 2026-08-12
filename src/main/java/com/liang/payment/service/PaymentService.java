package com.liang.payment.service;

import com.liang.order.entity.Order;
import com.liang.payment.dto.AbaPayWayResponse;
import com.liang.payment.dto.PaymentInitiationResult;
import com.liang.payment.entity.Payment;
import com.liang.shared.metadata.Metadata;

public interface PaymentService {

    PaymentInitiationResult initiatePayment(Order order, String paymentMethodRaw);
    // Creates ABA KHQR payment request and stores transaction details in DB
    AbaPayWayResponse createAbaKHQR(Metadata metadata, Long orderId);

    // Verifies payment status with ABA server and updates order status in DB
    AbaPayWayResponse checkAbaKHQR(Metadata metadata, Long orderId);
}
