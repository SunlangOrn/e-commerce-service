package com.liang.cart.controller;

import static com.liang.shared.api.ControllerHandler.responseCreated;
import static com.liang.shared.api.ControllerHandler.responseDeleted;
import static com.liang.shared.api.ControllerHandler.responseSucceed;

import com.liang.cart.dto.CartItemRequest;
import com.liang.cart.dto.CartResponse;
import com.liang.cart.service.CartService;
import com.liang.shared.entity.HttpBodyResponse;
import com.liang.shared.metadata.Metadata;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class PublicCartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<HttpBodyResponse<CartResponse>> viewMine() {
        return responseSucceed(cartService.viewMine(new Metadata()));
    }

    @PostMapping("/items")
    public ResponseEntity<HttpBodyResponse<CartResponse>> addItem(
            @Valid @RequestBody CartItemRequest request) {
        return responseCreated(cartService.addItem(new Metadata(), request));
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<HttpBodyResponse<CartResponse>> updateItem(
            @PathVariable Long itemId, @RequestParam Integer quantity) {
        return responseSucceed(cartService.updateItem(new Metadata(), itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<HttpBodyResponse<CartResponse>> removeItem(@PathVariable Long itemId) {
        return responseSucceed(cartService.removeItem(new Metadata(), itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clear() {
        cartService.clear(new Metadata());
        return responseDeleted();
    }
}