package com.liang.payment.aba;

import com.liang.payment.AbaPayWayProperties;
import com.liang.payment.dto.CheckTransactionRequest;
import com.liang.payment.dto.CheckTransactionResponse;
import com.liang.payment.dto.GenerateQrRequest;
import com.liang.payment.dto.GenerateQrResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AbaPayWayClient {
    private static final DateTimeFormatter REQ_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final AbaPayWayProperties properties;
    private final RestClient.Builder restClientBuilder;

    public String reqTimeNow() {
        return OffsetDateTime.now(ZoneOffset.UTC).format(REQ_TIME_FORMATTER);
    }

    public GenerateQrResponse generateQr(GenerateQrRequest requestWithoutHash) {
        String hash = sign(generateQrHashBody(requestWithoutHash));
        GenerateQrRequest request = requestWithoutHash.withHash(hash);

        return restClientBuilder.build()
                .post()
                .uri(properties.generateQrUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(GenerateQrResponse.class);
    }

    public CheckTransactionResponse checkTransaction(String tranId) {
        String reqTime = reqTimeNow();
        String hash = sign(reqTime + properties.merchantId() + tranId);
        CheckTransactionRequest request = new CheckTransactionRequest(
                reqTime,
                properties.merchantId(),
                tranId,
                hash
        );

        return restClientBuilder.build()
                .post()
                .uri(properties.checkTransactionUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CheckTransactionResponse.class);
    }

    private String generateQrHashBody(GenerateQrRequest r) {
        return empty(r.reqTime())
                + empty(r.merchantId())
                + empty(r.tranId())
                + empty(r.amount())
                + empty(r.items())
                + empty(r.firstname())
                + empty(r.lastname())
                + empty(r.email())
                + empty(r.phone())
                + empty(r.purchaseType())
                + empty(r.paymentOption())
                + empty(r.callbackUrl())
                + empty(r.currency())
                + empty(r.returnDeeplink())
                + empty(r.customFields())
                + empty(r.returnParams())
                + empty(r.payout())
                + empty(r.lifetime())
                + empty(r.qrImageTemplate());
    }

    private String sign(String raw) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(properties.apiKey().getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            return Base64.getEncoder().encodeToString(mac.doFinal(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot sign ABA PayWay request", e);
        }
    }

    private String empty(String value) {
        return value == null ? "" : value;
    }
}
