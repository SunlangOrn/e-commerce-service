package com.liang.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liang.order.entity.Order;
import com.liang.order.entity.OrderItem;
import com.liang.payment.AbaPayWayProperties;
import com.liang.payment.aba.AbaPayWayClient;
import com.liang.payment.dto.AbaPayWayResponse;
import com.liang.payment.dto.GenerateQrRequest;
import com.liang.payment.dto.GenerateQrResponse;
import com.liang.payment.dto.PaymentInitiationResult;
import com.liang.payment.entity.Payment;
import com.liang.payment.entity.PaymentMethod;
import com.liang.payment.entity.PaymentStatus;
import com.liang.payment.repository.PaymentRepository;
import com.liang.shared.api.NotFoundException;
import com.liang.shared.metadata.Metadata;
import com.liang.shared.metadata.MetadataHandler;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AbaPayWayClient abaPayWayClient;
    private final AbaPayWayProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PaymentInitiationResult initiatePayment(Order order, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency(order.getCurrency());

        payment = paymentRepository.save(payment);

        AbaPayWayResponse abaResponse = null;
        if (paymentMethod == PaymentMethod.ABA_PAYWAY_KHQR) {
            abaResponse = initiateAbaQr(payment);
        }

        return new PaymentInitiationResult(payment, abaResponse);
    }

    @Override
    @MetadataHandler
    @Transactional
    public AbaPayWayResponse createAbaKHQR(Metadata metadata, Long orderId) {
        Payment payment = paymentRepository.findByOrderIdAndOrderUserId(orderId, metadata.getUserId())
                .orElseThrow(() -> new NotFoundException("Payment not found for order id: " + orderId));

        if (payment.getPaymentMethod() != PaymentMethod.ABA_PAYWAY_KHQR) {
            throw new IllegalStateException("This order is not ABA PayWay KHQR payment");
        }
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("This order is already paid");
        }

        return initiateAbaQr(payment);
    }

    @Override
    @MetadataHandler
    @Transactional
    public AbaPayWayResponse checkAbaKHQR(Metadata metadata, Long orderId) {
        Payment payment = paymentRepository.findByOrderIdAndOrderUserId(orderId, metadata.getUserId())
                .orElseThrow(() -> new NotFoundException("Payment not found for order id: " + orderId));

        if (payment.getTransactionReference() == null || payment.getTransactionReference().isBlank()) {
            throw new IllegalStateException("ABA transaction has not been created yet");
        }

        var response = abaPayWayClient.checkTransaction(payment.getTransactionReference());

        boolean paid = response != null
                && response.data() != null
                && response.data().paymentStatusCode() != null
                && response.data().paymentStatusCode() == 0;

        if (paid) {
            payment.markAsPaid(payment.getTransactionReference());
            paymentRepository.save(payment);
        }

        return AbaPayWayResponse.builder()
                .orderId(orderId)
                .tranId(payment.getTransactionReference())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .statusCode(response != null && response.status() != null ? response.status().code() : null)
                .statusMessage(response != null && response.status() != null ? response.status().message() : null)
                .build();
    }

    private AbaPayWayResponse initiateAbaQr(Payment payment) {
        Order order = payment.getOrder();
        String tranId = buildTranId(order.getId());

        payment.setTransactionReference(tranId);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        GenerateQrRequest request = new GenerateQrRequest(
                abaPayWayClient.reqTimeNow(),
                properties.merchantId(),
                tranId,
                payment.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                encodeOrderItems(order),
                firstName(order.getShippingFullName()),
                lastName(order.getShippingFullName()),
                order.getUser().getEmail(),
                order.getShippingPhone(),
                "purchase",
                "abapay_khqr",
                base64OrNull(properties.callbackUrl()),
                properties.currency(),
                "",
                "",
                "",
                "",
                String.valueOf(properties.lifetimeMinutes()),
                "template3_color",
                null
        );

        GenerateQrResponse response = abaPayWayClient.generateQr(request);

        return AbaPayWayResponse.builder()
                .orderId(order.getId())
                .tranId(tranId)
                .amount(payment.getAmount())
                .currency(properties.currency())
                .qrString(response != null ? response.qrString() : null)
                .qrImage(response != null ? response.qrImage() : null)
                .abaPayDeeplink(response != null ? response.abaPayDeeplink() : null)
                .statusCode(response != null && response.status() != null ? response.status().code() : null)
                .statusMessage(response != null && response.status() != null ? response.status().message() : null)
                .expiresAt(LocalDateTime.now().plusMinutes(properties.lifetimeMinutes()))
                .build();
    }

    private String buildTranId(Long orderId) {
        return "ORD" + orderId + System.currentTimeMillis() % 1_000_000L;
    }

    private String encodeOrderItems(Order order) {
        try {
            List<Map<String, Object>> items = order.getOrderItems().stream()
                    .map(this::mapItem)
                    .toList();
            String json = objectMapper.writeValueAsString(items);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot build ABA PayWay items", e);
        }
    }

    private Map<String, Object> mapItem(OrderItem item) {
        return Map.of(
                "name", item.getProductName(),
                "quantity", item.getQuantity(),
                "price", item.getPrice().setScale(2, RoundingMode.HALF_UP)
        );
    }

    private String base64OrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "Customer";
        }
        return fullName.trim().split("\\s+")[0];
    }

    private String lastName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "Ecommerce";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : "Customer";
    }
}
