package com.liang.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.liang.payment.dto.AbaPayWayResponse;
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
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String paymentStatus;
    private String paymentMethod;
    private Integer totalItems;
    private List<OrderItemResponse> items;
    private Instant createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AbaPayWayResponse abaPayWayResponse;
}
