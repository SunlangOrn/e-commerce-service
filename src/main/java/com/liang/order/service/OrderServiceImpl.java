package com.liang.order.service;

import com.liang.address.entity.Address;
import com.liang.address.repository.AddressRepository;
import com.liang.cart.entity.Cart;
import com.liang.cart.entity.CartItem;
import com.liang.cart.repository.CartRepository;
import com.liang.order.dto.OrderRequest;
import com.liang.order.dto.OrderRequestUpdate;
import com.liang.order.dto.OrderResponse;
import com.liang.order.dto.OrderResponseDetail;
import com.liang.order.entity.Order;
import com.liang.order.entity.OrderItem;
import com.liang.order.entity.OrderStatus;
import com.liang.order.mapper.OrderMapper;
import com.liang.order.repository.OrderRepository;
import com.liang.payment.AbaPayWayProperties;
import com.liang.payment.aba.AbaPayWayClient;
import com.liang.payment.dto.AbaPayWayResponse;
import com.liang.payment.dto.GenerateQrRequest;
import com.liang.payment.dto.GenerateQrResponse;
import com.liang.payment.entity.Payment;
import com.liang.payment.entity.PaymentMethod;
import com.liang.payment.entity.PaymentStatus;
import com.liang.product.entity.Product;
import com.liang.product.repository.ProductRepository;
import com.liang.shared.api.NotFoundException;
import com.liang.shared.metadata.Metadata;
import com.liang.shared.metadata.MetadataHandler;
import com.liang.shared.security.User;
import com.liang.shared.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    private final AbaPayWayClient abaPayWayClient;
    private final AbaPayWayProperties abaProperties;

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
    public OrderResponseDetail adminView(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
        return orderMapper.mapDetail(order);
    }

    @Override
    @MetadataHandler
    @Transactional
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
    @MetadataHandler
    @Transactional
    public OrderResponse checkout(Metadata metadata, OrderRequest request) {
        Long userId = metadata.getUserId();
        User user = userRepository.getReferenceById(userId);

        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new NotFoundException("Delivery address not found for current user"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Cart not found for user"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart");
        }

        Order order = new Order();
        order.setOrderNumber("ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        order.setUser(user);
        order.snapshotAddress(address);

        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setSubtotal(itemSubtotal);

            order.addOrderItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        AbaPayWayResponse qrResponse = null;

        if ("CASH_ON_DELIVERY".equalsIgnoreCase(request.getPaymentMethod())) {
            Payment cash = new Payment();
            cash.setOrder(savedOrder);
            cash.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
            cash.setPaymentStatus(PaymentStatus.PENDING);
            cash.setAmount(savedOrder.getTotalAmount());

            savedOrder.setPayment(cash);
            orderRepository.save(savedOrder);

        } else if ("ABA_PAYWAY_KHQR".equalsIgnoreCase(request.getPaymentMethod())) {
            // FIX 1: Generate tranId strictly under 20 characters
            String tranId = buildTranId(savedOrder.getId());

            Payment qrPayment = new Payment();
            qrPayment.setOrder(savedOrder);
            qrPayment.setPaymentMethod(PaymentMethod.ABA_PAYWAY_KHQR);
            qrPayment.setPaymentStatus(PaymentStatus.PENDING);
            qrPayment.setAmount(savedOrder.getTotalAmount());
            qrPayment.setTransactionReference(tranId);

            savedOrder.setPayment(qrPayment);
            orderRepository.save(savedOrder);

            String currency = abaProperties.currency().toUpperCase();
            boolean isKHR = "KHR".equals(currency);

            BigDecimal formattedAmount = isKHR
                    ? savedOrder.getTotalAmount().setScale(0, RoundingMode.HALF_UP)
                    : savedOrder.getTotalAmount().setScale(2, RoundingMode.HALF_UP);

            String reqTime = abaPayWayClient.reqTimeNow();

            String hash = abaPayWayClient.generateHash(
                    reqTime,
                    abaProperties.merchantId(),
                    tranId,
                    formattedAmount,
                    "abapay_khqr",
                    currency
            );

            // FIX 3: Construct request with dynamic currency and exact 9 parameters
            GenerateQrRequest qrRequest = new GenerateQrRequest(
                    reqTime,
                    abaProperties.merchantId(),
                    tranId,
                    formattedAmount,
                    "abapay_khqr",
                    currency,
                    hash,
                    abaProperties.lifetimeMinutes(),
                    "template3_color"
            );

            GenerateQrResponse abaResponse = abaPayWayClient.generateQr(qrRequest);

            qrResponse = AbaPayWayResponse.builder()
                    .orderId(savedOrder.getId())
                    .tranId(tranId)
                    .amount(savedOrder.getTotalAmount())
                    .currency(currency)
                    .qrString(abaResponse != null ? abaResponse.qrString() : null)
                    .qrImage(abaResponse != null ? abaResponse.qrImage() : null)
                    .abaPayDeeplink(abaResponse != null ? abaResponse.abaPayDeeplink() : null)
                    .statusCode(abaResponse != null && abaResponse.status() != null ? abaResponse.status().code() : null)
                    .statusMessage(abaResponse != null && abaResponse.status() != null ? abaResponse.status().message() : null)
                    .expiresAt(LocalDateTime.now().plusMinutes(abaProperties.lifetimeMinutes()))
                    .build();
        }

        // Clear user cart
        cart.getItems().clear();
        cartRepository.save(cart);

        OrderResponse response = orderMapper.fromOrder(savedOrder);

        if (qrResponse != null) {
            response.setAbaPayWayResponse(qrResponse);
        }

        return response;
    }

    @Override
    @MetadataHandler
    public Page<OrderResponse> list(Metadata metadata, Pageable pageable) {
        return orderRepository.findByUserId(metadata.getUserId(), pageable)
                .map(orderMapper::fromOrder);
    }

    @Override
    @MetadataHandler
    public OrderResponse view(Metadata metadata, Long id) {
        Order order = orderRepository.findByIdAndUserId(id, metadata.getUserId())
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
        return orderMapper.fromOrder(order);
    }

    @Override
    @MetadataHandler
    @Transactional
    public OrderResponse cancel(Metadata metadata, Long id) {
        Order order = orderRepository.findByIdAndUserId(id, metadata.getUserId())
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only orders with PENDING status can be cancelled");
        }

        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Cannot cancel an order that has already been paid");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.CANCELLED);

        if (order.getPayment() != null) {
            order.getPayment().setPaymentStatus(PaymentStatus.CANCELLED);
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.fromOrder(savedOrder);
    }

    // Helper method to keep total length <= 20 characters
    private String buildTranId(Long orderId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        int randomSuffix = ThreadLocalRandom.current().nextInt(100, 999);
        String rawTranId = "O" + orderId + "T" + timestamp + randomSuffix;

        return rawTranId.length() > 20 ? rawTranId.substring(0, 20) : rawTranId;
    }
}