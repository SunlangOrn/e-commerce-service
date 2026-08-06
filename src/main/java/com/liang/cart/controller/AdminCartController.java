package com.liang.cart.controller;

import com.liang.cart.dto.CartResponseDetail;
import com.liang.cart.service.CartService;
import com.liang.shared.entity.HttpBodyPagingResponse;
import com.liang.shared.entity.HttpBodyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.liang.shared.api.ControllerHandler.responsePaging;
import static com.liang.shared.api.ControllerHandler.responseSucceed;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/v1/carts")
public class AdminCartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<HttpBodyResponse<List<CartResponseDetail>>> getAllCarts(Pageable pageable) {

        Page<CartResponseDetail> carts = cartService.getAllUserCarts(pageable);

        return responsePaging(
                carts.getContent(),
                HttpBodyPagingResponse.of(
                        carts.getNumber(),
                        carts.getSize(),
                        carts.getTotalElements(),
                        carts.getTotalPages()));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<HttpBodyResponse<CartResponseDetail>> getCartByUserId(@PathVariable Long userId) {
        return responseSucceed(cartService.getCartByUserId(userId));
    }
}
