package com.liang.cart.controller;

import com.liang.cart.dto.CartResponse;
import com.liang.cart.service.CartService;
import com.liang.shared.entity.HttpBodyResponse;
import com.liang.shared.metadata.Metadata;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.liang.shared.api.ControllerHandler.responseSucceed;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/v1/carts")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<HttpBodyResponse<Page<CartResponse>>> getAllCarts(Pageable pageable) {
        return responseSucceed(cartService.getAllCarts(pageable));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<HttpBodyResponse<CartResponse>> getCartByUserId(Metadata metadata) {
        return responseSucceed(cartService.getCartByUserId(new Metadata()));
    }
}
