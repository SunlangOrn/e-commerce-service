package com.liang.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GenerateQrRequest(
        @JsonProperty("req_time") String reqTime,
        @JsonProperty("merchant_id") String merchantId,
        @JsonProperty("tran_id") String tranId,
        String amount,
        String items,
        @JsonProperty("first_name") String firstname,
        @JsonProperty("last_name") String lastname,
        String email,
        String phone,
        @JsonProperty("purchase_type") String purchaseType,
        @JsonProperty("payment_option") String paymentOption,
        @JsonProperty("callback_url") String callbackUrl,
        String currency,
        @JsonProperty("return_deeplink") String returnDeeplink,
        @JsonProperty("custom_fields") String customFields,
        @JsonProperty("return_params") String returnParams,
        String payout,
        String lifetime,
        @JsonProperty("qr_image_template") String qrImageTemplate,
        String hash
) {
    public GenerateQrRequest withHash(String newHash) {
        return new GenerateQrRequest(
                reqTime, merchantId, tranId, amount, items, firstname, lastname, email, phone,
                purchaseType, paymentOption, callbackUrl, currency, returnDeeplink, customFields,
                returnParams, payout, lifetime, qrImageTemplate, newHash
        );
    }
}
