package com.liang.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GenerateQrResponse(
        Status status,
        String qrString,
        String qrImage,
        @JsonProperty("abapay_deeplink") String abaPayDeeplink
) {}
