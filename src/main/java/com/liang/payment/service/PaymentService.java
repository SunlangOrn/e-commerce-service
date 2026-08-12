package com.liang.payment.service;

import com.liang.order.entity.Order;
import com.liang.payment.dto.AbaPayWayResponse;
import com.liang.payment.dto.PaymentInitiationResult;
import com.liang.payment.entity.PaymentMethod;
import com.liang.shared.metadata.Metadata;

public interface PaymentService {
    PaymentInitiationResult initiatePayment(Order order, PaymentMethod paymentMethod);

    AbaPayWayResponse createAbaKHQR(Metadata metadata, Long orderId);

    AbaPayWayResponse checkAbaKHQR(Metadata metadata, Long orderId);
}
