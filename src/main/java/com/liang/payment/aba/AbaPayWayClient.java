package com.liang.payment.aba;

import com.liang.payment.AbaPayWayProperties;
import com.liang.payment.dto.CheckTransactionResponse;
import com.liang.payment.dto.GenerateQrRequest;
import com.liang.payment.dto.GenerateQrResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
@Component
@Slf4j
public class AbaPayWayClient {

    private final RestTemplate restTemplate;
    private final AbaPayWayProperties properties;

    public String reqTimeNow() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public GenerateQrResponse generateQr(GenerateQrRequest request) {
        return restTemplate.postForObject(properties.apiUrl(), request, GenerateQrResponse.class);
    }

    public CheckTransactionResponse checkTransaction(String tranId) {
        String url = properties.checkTransactionUrl() + "?tran_id=" + tranId + "&merchant_id=" + properties.merchantId();
        return restTemplate.getForObject(url, CheckTransactionResponse.class);
    }

    public String generateHash(
            String reqTime,
            String merchantId,
            String tranId,
            String formattedAmount,
            String items,
            String paymentOption,
            String currency,
            Integer lifetime,
            String qrType
    ) {
        // Concatenate all request body fields in exact ABA PayWay sequence
        StringBuilder sb = new StringBuilder();
        sb.append(reqTime)
                .append(merchantId)
                .append(tranId)
                .append(formattedAmount);

        if (items != null && !items.isBlank()) {
            sb.append(items);
        }

        sb.append(paymentOption)
                .append(currency);

        if (lifetime != null) {
            sb.append(lifetime);
        }
        if (qrType != null && !qrType.isBlank()) {
            sb.append(qrType);
        }

        String dataToHash = sb.toString();
        log.debug("ABA PayWay Concatenated Hash Input: [{}]", dataToHash);

        try {
            Mac hmacSha512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    properties.apiKey().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );
            hmacSha512.init(secretKey);

            byte[] hashBytes = hmacSha512.doFinal(dataToHash.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate ABA PayWay HMAC-SHA512 hash", e);
        }
    }
}