package com.liang.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.aba-payway")
public record AbaPayWayProperties(
        String generateQrUrl,
        String checkTransactionUrl,
        String merchantId,
        String apiKey,
        String callbackUrl,
        String currency,
        int lifetimeMinutes
) {
    public AbaPayWayProperties {
        if (currency == null || currency.isBlank()) currency = "KHR";
        if (lifetimeMinutes <= 0) lifetimeMinutes = 15;
    }
}
