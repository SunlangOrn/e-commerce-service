package com.liang.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CartResponse {
    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal total;
}