package com.liang.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private String userEmail;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal amount;
    private String currency;
    private String transactionReference;
    private Instant paidAt;
    private Instant createdAt;
    private Instant updatedAt;
}