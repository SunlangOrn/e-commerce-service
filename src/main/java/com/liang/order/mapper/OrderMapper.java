package com.liang.order.mapper;

import com.liang.order.dto.OrderItemResponse;
import com.liang.order.dto.OrderResponse;
import com.liang.order.dto.OrderResponseDetail;
import com.liang.order.entity.Order;
import com.liang.order.entity.OrderItem;
import com.liang.order.entity.OrderStatus;
import com.liang.payment.entity.PaymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "orderStatus", source = "orderStatus", qualifiedByName = "orderStatusToString")
    @Mapping(target = "paymentStatus", source = "paymentStatus", qualifiedByName = "paymentStatusToString")
    @Mapping(target = "totalItems", expression = "java(order.getOrderItems() != null ? order.getOrderItems().size() : 0)")
    OrderResponse fromOrder(Order order);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "orderStatus", source = "orderStatus", qualifiedByName = "orderStatusToString")
    @Mapping(target = "paymentStatus", source = "paymentStatus", qualifiedByName = "paymentStatusToString")
    OrderResponseDetail mapDetail(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImage", source = "product.imageUrl")
    OrderItemResponse fromOrderItem(OrderItem orderItem);

    @Named("orderStatusToString")
    default String mapOrderStatusToString(OrderStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToOrderStatus")
    default OrderStatus mapStringToOrderStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Order Status: " + status);
        }
    }

    @Named("paymentStatusToString")
    default String mapPaymentStatusToString(PaymentStatus paymentStatus) {
        return paymentStatus != null ? paymentStatus.name() : null;
    }

    @Named("stringToPaymentStatus")
    default PaymentStatus mapStringToPaymentStatus(String paymentStatus) {
        if (paymentStatus == null || paymentStatus.isBlank()) {
            return null;
        }
        try {
            return PaymentStatus.valueOf(paymentStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Payment Status: " + paymentStatus);
        }
    }
}