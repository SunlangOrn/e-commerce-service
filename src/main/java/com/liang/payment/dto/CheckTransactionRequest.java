package com.liang.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CheckTransactionRequest(
        @JsonProperty("req_time") String reqTime,
        @JsonProperty("merchant_id") String merchantId,
        @JsonProperty("tran_id") String tranId,
        String hash
) {}
