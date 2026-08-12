package com.liang.payment.service;

import com.liang.order.entity.Order;
import com.liang.payment.AbaPayWayProperties;
import com.liang.payment.aba.AbaPayWayClient;
import com.liang.payment.dto.*;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private static final String PAYMENT_OPTION_KHQR = "abapay_khqr";
    private static final String QR_TYPE_COLOR = "template3_color";

    private final PaymentRepository paymentRepository;
    private final AbaPayWayClient abaPayWayClient;
    private final AbaPayWayProperties properties;

    @Override
    @Transactional
    public PaymentInitiationResult initiatePayment(Order order, String paymentMethodRaw) {
        PaymentMethod method = PaymentMethod.valueOf(paymentMethodRaw.toUpperCase());
        String currency = properties.currency().toUpperCase();

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency(currency);

        order.setPayment(payment);
        order.setCurrency(currency);

        AbaPayWayResponse qrResponse = null;
        if (method == PaymentMethod.ABA_PAYWAY_KHQR) {
            qrResponse = generateQrInternal(payment);
            payment.setTransactionReference(qrResponse.getTranId());
        }

        Payment saved = paymentRepository.save(payment);
        return new PaymentInitiationResult(saved, qrResponse);
    }

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

        AbaPayWayResponse response = generateQrInternal(payment);
        payment.setTransactionReference(response.getTranId());
        paymentRepository.save(payment);
        return response;
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
                    .currency(payment.getCurrency())
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

    /**
     * Prepares parameters, generates the cryptographic hash, and executes the call to ABA PayWay.
     */
    private AbaPayWayResponse generateQrInternal(Payment payment) {
        String currency = properties.currency().toUpperCase();
        boolean isKHR = "KHR".equals(currency);

        // Format amount strictly according to currency rule
        String formattedAmount = isKHR
                ? payment.getAmount().setScale(0, RoundingMode.HALF_UP).toPlainString()
                : payment.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString();

        String tranId = buildTranId(payment.getOrder().getId());
        String reqTime = abaPayWayClient.reqTimeNow();

        // Base64 encode empty items array as required by ABA purchase API
        String itemsBase64 = Base64.getEncoder().encodeToString("[]".getBytes(StandardCharsets.UTF_8));
        Integer lifetime = properties.lifetimeMinutes();

        String hash = abaPayWayClient.generateHash(
                reqTime,
                properties.merchantId(),
                tranId,
                formattedAmount,
                itemsBase64,
                PAYMENT_OPTION_KHQR,
                currency,
                lifetime,
                QR_TYPE_COLOR
        );

        GenerateQrRequest request = new GenerateQrRequest(
                reqTime,
                properties.merchantId(),
                tranId,
                formattedAmount,
                itemsBase64,
                PAYMENT_OPTION_KHQR,
                currency,
                hash,
                lifetime,
                QR_TYPE_COLOR
        );

        log.info("Sending ABA PayWay Generate QR Request: tranId={}, amount={}, currency={}", tranId, formattedAmount, currency);
        GenerateQrResponse response = abaPayWayClient.generateQr(request);

        return AbaPayWayResponse.builder()
                .orderId(payment.getOrder().getId())
                .tranId(tranId)
                .amount(payment.getAmount())
                .currency(currency)
                .qrString(response != null ? response.qrString() : null)
                .qrImage(response != null ? response.qrImage() : null)
                .abaPayDeeplink(response != null ? response.abaPayDeeplink() : null)
                .statusCode(response != null && response.status() != null ? response.status().code() : null)
                .statusMessage(response != null && response.status() != null ? response.status().message() : null)
                .expiresAt(LocalDateTime.now().plusMinutes(lifetime))
                .build();
    }

    private Payment findUserPayment(Long orderId, Long userId) {
        return paymentRepository.findByOrderIdAndOrderUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Payment not found for order id: " + orderId));
    }

    private String buildTranId(Long orderId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        int randomSuffix = ThreadLocalRandom.current().nextInt(100, 999);
        String rawTranId = "O" + orderId + "T" + timestamp + randomSuffix;
        if (rawTranId.length() > 20) {
            log.warn("Generated ABA tranId exceeded 20 chars, truncating: {}", rawTranId);
        }
        return rawTranId.length() > 20 ? rawTranId.substring(0, 20) : rawTranId;
    }
}