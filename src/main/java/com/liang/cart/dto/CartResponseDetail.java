package com.liang.cart.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponseDetail {
    private Long id;
    private Long userId;
    private List<CartItemResponse> items;
    private BigDecimal total;
}
