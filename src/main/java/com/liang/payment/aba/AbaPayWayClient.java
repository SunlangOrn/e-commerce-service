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
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        // Call ABA PayWay purchase API endpoint
        return restTemplate.postForObject(properties.apiUrl(), request, GenerateQrResponse.class);
    }

    public CheckTransactionResponse checkTransaction(String tranId) {
        // Call ABA PayWay check-transaction API endpoint
        String url = properties.checkTransactionUrl() + "?tran_id=" + tranId + "&merchant_id=" + properties.merchantId();
        return restTemplate.getForObject(url, CheckTransactionResponse.class);
    }

    public String generateHash(
            String reqTime,
            String merchantId,
            String tranId,
            BigDecimal amount,
            String paymentOption,
            String currency
    ) {
        // 1. Format amount strictly according to currency
        String formattedAmount = "KHR".equalsIgnoreCase(currency)
                ? amount.setScale(0, RoundingMode.HALF_UP).toPlainString()
                : amount.setScale(2, RoundingMode.HALF_UP).toPlainString();

        // 2. Concatenate fields in exact order required by ABA PayWay v3
        // req_time + merchant_id + tran_id + amount + payment_option + currency
        String dataToHash = reqTime + merchantId + tranId + formattedAmount + paymentOption + currency;

        log.debug("ABA PayWay Concatenated Hash Input: [{}]", dataToHash);

        try {
            // 3. Perform HMAC-SHA512 using the Secret Key
            Mac hmacSha512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    properties.apiKey().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );
            hmacSha512.init(secretKey);

            byte[] hashBytes = hmacSha512.doFinal(dataToHash.getBytes(StandardCharsets.UTF_8));

            // 4. Base64 Encode
            return Base64.getEncoder().encodeToString(hashBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate ABA PayWay HMAC-SHA512 hash", e);
        }
    }

}