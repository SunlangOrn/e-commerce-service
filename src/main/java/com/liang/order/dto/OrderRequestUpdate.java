package com.liang.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestUpdate {

    @NotBlank(message = "Order status is required")
    private String orderStatus; // PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED

    private String paymentStatus;
}
