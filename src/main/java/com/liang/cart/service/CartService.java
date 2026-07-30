package com.liang.cart.service;

import com.liang.cart.dto.CartItemRequest;
import com.liang.cart.dto.CartResponse;
import com.liang.shared.metadata.Metadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartService {

    Page<CartResponse> getAllCarts(Pageable pageable);

    CartResponse getCartByUserId(Metadata metadata);

    CartResponse viewMine(Metadata metadata);

    CartResponse addItem(Metadata metadata, CartItemRequest request);

    CartResponse updateItem(Metadata metadata, Long itemId, Integer quantity);

    CartResponse removeItem(Metadata metadata, Long itemId);

    void clear(Metadata metadata);
}