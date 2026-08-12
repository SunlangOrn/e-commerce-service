package com.liang.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record GenerateQrRequest(

        @JsonProperty("req_time")
        String reqTime,

        @JsonProperty("merchant_id")
        String merchantId,

        @JsonProperty("tran_id")
        String tranId,

        @JsonProperty("amount")
        BigDecimal amount,

        @JsonProperty("payment_option")
        String paymentOption,

        @JsonProperty("currency")
        String currency,

        @JsonProperty("hash")
        String hash,

        @JsonProperty("lifetime")
        Integer lifetime,

        @JsonProperty("qr_type")
        String qrType
) {
}