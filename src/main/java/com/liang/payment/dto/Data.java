package com.liang.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Data(
        @JsonProperty("payment_status_code") Integer paymentStatusCode,
        @JsonProperty("payment_status") String paymentStatus,
        @JsonProperty("payment_amount") BigDecimal paymentAmount,
        @JsonProperty("payment_currency") String paymentCurrency,
        String apv
) {}
