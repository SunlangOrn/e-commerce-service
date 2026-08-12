package com.liang.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDetail {

    private Long id;
    private String orderNumber;
    private String orderStatus;
    private String paymentStatus;
    private String paymentMethod;

    private Long userId;
    private String userName;
    private String userEmail;

    private String shippingFullName;
    private String shippingPhone;
    private String shippingAddressLine;
    private String shippingCity;
    private String shippingProvince;
    private String shippingPostalCode;

    private String note;

    private BigDecimal totalAmount;
    private String currency;

    private List<OrderItemResponse> items;

    private Instant createdAt;
    private Instant updatedAt;
}
