package com.liang.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.aba-payway")
public record AbaPayWayProperties (

        String apiUrl,
        String checkTransactionUrl,
        String merchantId,
        String apiKey,
        String callbackUrl,
        String currency,
        Integer lifetimeMinutes
){}
