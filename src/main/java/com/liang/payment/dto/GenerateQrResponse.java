package com.liang.payment.dto;

public record GenerateQrResponse(

        Status status,
        String qrString,
        String qrImage,
        String abaPayDeeplink
) {
}


