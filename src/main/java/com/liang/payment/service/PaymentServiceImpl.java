package com.liang.payment.service;

import com.liang.payment.AbaPayWayProperties;
import com.liang.payment.aba.AbaPayWayClient;
import com.liang.payment.dto.AbaPayWayResponse;
import com.liang.payment.dto.CheckTransactionResponse;
import com.liang.payment.dto.GenerateQrRequest;
import com.liang.payment.dto.GenerateQrResponse;
import com.liang.payment.entity.Payment;
import com.liang.payment.entity.PaymentMethod;
import com.liang.payment.entity.PaymentStatus;
import com.liang.payment.repository.PaymentRepository;
import com.liang.shared.api.NotFoundException;
import com.liang.shared.metadata.Metadata;
import com.liang.shared.metadata.MetadataHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AbaPayWayClient abaPayWayClient;
    private final AbaPayWayProperties properties;

    @MetadataHandler
    @Transactional
    @Override
    public AbaPayWayResponse createAbaKHQR(Metadata metadata, Long orderId) {
        Payment payment = findUserPayment(orderId, metadata.getUserId());

        if (payment.getPaymentMethod() != PaymentMethod.ABA_PAYWAY_KHQR) {
            throw new IllegalStateException("This order is not configured for ABA PayWay KHQR payment");
        }

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("This order is already paid");
        }

        String tranId = buildTranId(orderId);
        payment.setTransactionReference(tranId);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        String currency = properties.currency().toUpperCase();
        boolean isKHR = "KHR".equals(currency);

        var formattedAmount = isKHR
                ? payment.getAmount().setScale(0, RoundingMode.HALF_UP)
                : payment.getAmount().setScale(2, RoundingMode.HALF_UP);

        String reqTime = abaPayWayClient.reqTimeNow();

        // 1. Calculate HMAC-SHA512 Hash
        String hash = abaPayWayClient.generateHash(
                reqTime,
                properties.merchantId(),
                tranId,
                formattedAmount,
                "abapay_khqr",
                currency
        );

        // 2. Pass EXACTLY 9 arguments matching GenerateQrRequest definition
        GenerateQrRequest request = new GenerateQrRequest(
                reqTime,
                properties.merchantId(),
                tranId,
                formattedAmount,
                "abapay_khqr",
                currency,
                hash,
                properties.lifetimeMinutes(),
                "template3_color"
        );

        GenerateQrResponse response = abaPayWayClient.generateQr(request);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(properties.lifetimeMinutes());

        return AbaPayWayResponse.builder()
                .orderId(orderId)
                .tranId(tranId)
                .amount(payment.getAmount())
                .currency(currency)
                .qrString(response != null ? response.qrString() : null)
                .qrImage(response != null ? response.qrImage() : null)
                .abaPayDeeplink(response != null ? response.abaPayDeeplink() : null)
                .statusCode(response != null && response.status() != null ? response.status().code() : null)
                .statusMessage(response != null && response.status() != null ? response.status().message() : null)
                .expiresAt(expiresAt)
                .build();
    }

    @MetadataHandler
    @Transactional
    @Override
    public AbaPayWayResponse checkAbaKHQR(Metadata metadata, Long orderId) {
        Payment payment = findUserPayment(orderId, metadata.getUserId());

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return AbaPayWayResponse.builder()
                    .orderId(orderId)
                    .tranId(payment.getTransactionReference())
                    .amount(payment.getAmount())
                    .currency(properties.currency())
                    .statusCode("0")
                    .statusMessage("Transaction already verified and paid")
                    .build();
        }

        if (payment.getTransactionReference() == null || payment.getTransactionReference().isBlank()) {
            throw new IllegalStateException("ABA transaction has not been created yet");
        }

        CheckTransactionResponse response = abaPayWayClient.checkTransaction(payment.getTransactionReference());

        boolean paid = response != null
                && response.data() != null
                && response.data().paymentStatusCode() != null
                && response.data().paymentStatusCode() == 0;

        if (paid) {
            payment.markAsPaid(payment.getTransactionReference());

            if (payment.getOrder() != null) {
                payment.getOrder().setPaymentStatus(PaymentStatus.SUCCESS);
            }

            paymentRepository.save(payment);
        }

        return AbaPayWayResponse.builder()
                .orderId(orderId)
                .tranId(payment.getTransactionReference())
                .amount(payment.getAmount())
                .currency(properties.currency())
                .statusCode(response != null && response.status() != null ? response.status().code() : null)
                .statusMessage(response != null && response.status() != null ? response.status().message() : null)
                .build();
    }

    private Payment findUserPayment(Long orderId, Long userId) {
        return paymentRepository.findByOrderIdAndOrderUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Payment not found for order id: " + orderId));
    }

    // FIX: Keep total length strictly <= 20 characters for ABA PayWay limits
    private String buildTranId(Long orderId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        int randomSuffix = ThreadLocalRandom.current().nextInt(100, 999);
        String rawTranId = "O" + orderId + "T" + timestamp + randomSuffix;

        return rawTranId.length() > 20 ? rawTranId.substring(0, 20) : rawTranId;
    }
}