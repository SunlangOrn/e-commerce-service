package com.liang.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckTransactionResponse(
        Integer status,
        String description,
        BigDecimal amount,
        BigDecimal totalAmount,
        String apv,
        @JsonProperty("payment_status") String paymentStatus,
        String datetime
) {}