package com.liang.cart.mapper;

import com.liang.cart.dto.CartItemResponse;
import com.liang.cart.dto.CartResponse;
import com.liang.cart.dto.CartResponseDetail;
import com.liang.cart.entity.Cart;
import com.liang.cart.entity.CartItem;
import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartMapper {

    @Mapping(target = "total", source = "items", qualifiedByName = "calculateTotal")
    CartResponse toCartResponse(Cart cart);

    @Mapping(target = "total", source = "items", qualifiedByName = "calculateTotal")
    CartResponseDetail toCartResponseDetail(Cart cart);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "subtotal", source = ".", qualifiedByName = "calculateSubtotal")
    CartItemResponse toCartItemResponse(CartItem item);

    @Named("calculateSubtotal")
    default BigDecimal calculateSubtotal(CartItem item) {
        if (item == null || item.getProduct() == null || item.getProduct().getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    @Named("calculateTotal")
    default BigDecimal calculateTotal(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(this::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}