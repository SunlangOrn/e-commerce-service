package com.liang.order.service;

import com.liang.address.repository.AddressRepository;
import com.liang.cart.repository.CartRepository;
import com.liang.order.dto.OrderRequest;
import com.liang.order.dto.OrderRequestUpdate;
import com.liang.order.dto.OrderResponse;
import com.liang.order.dto.OrderResponseDetail;
import com.liang.order.entity.Order;
import com.liang.order.entity.OrderStatus;
import com.liang.order.mapper.OrderMapper;
import com.liang.order.repository.OrderRepository;
import com.liang.payment.entity.PaymentStatus;
import com.liang.product.repository.ProductRepository;
import com.liang.shared.api.NotFoundException;
import com.liang.shared.metadata.Metadata;
import com.liang.shared.metadata.MetadataHandler;
import com.liang.shared.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private  final OrderMapper orderMapper;

    @Override
    @MetadataHandler
    public Page<OrderResponseDetail> adminList(Long filterUserId, String orderStatus, Pageable pageable) {

        OrderStatus statusEnum = orderMapper.mapStringToOrderStatus(orderStatus);

        if (filterUserId != null && statusEnum != null) {
            return orderRepository.findByUserIdAndOrderStatus(filterUserId, statusEnum, pageable)
                    .map(orderMapper::mapDetail);
        } else if (filterUserId != null) {
            return orderRepository.findByUserId(filterUserId, pageable)
                    .map(orderMapper::mapDetail);
        } else if (statusEnum != null) {
            return orderRepository.findByOrderStatus(statusEnum, pageable)
                    .map(orderMapper::mapDetail);
        }

        return orderRepository.findAll(pageable)
                .map(orderMapper::mapDetail);
    }

    @Override
    @MetadataHandler
    @Transactional
    public OrderResponseDetail adminView(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
        return orderMapper.mapDetail(order);
    }

    @Override
    public OrderResponseDetail adminUpdateStatus(Long id, OrderRequestUpdate requestUpdate) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));

        if (requestUpdate.getOrderStatus() != null && !requestUpdate.getOrderStatus().isBlank()) {
            OrderStatus newOrderStatus = orderMapper.mapStringToOrderStatus(requestUpdate.getOrderStatus());
            order.setOrderStatus(newOrderStatus);
        }

        if (requestUpdate.getPaymentStatus() != null && !requestUpdate.getPaymentStatus().isBlank()) {
            PaymentStatus newPaymentStatus = orderMapper.mapStringToPaymentStatus(requestUpdate.getPaymentStatus());
            order.setPaymentStatus(newPaymentStatus);
        }

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.mapDetail(updatedOrder);
    }

    @Override
    public OrderResponse checkout(Metadata metadata, OrderRequest request) {
        return null;
    }

    @Override
    public Page<OrderResponse> list(Metadata metadata, Pageable pageable) {
        return null;
    }

    @Override
    public OrderResponse view(Metadata metadata, Long id) {
        return null;
    }

    @Override
    public OrderResponse cancel(Metadata metadata, Long id) {
        return null;
    }
}