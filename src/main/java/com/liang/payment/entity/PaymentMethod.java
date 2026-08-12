package com.liang.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    ABA_PAYWAY_KHQR("abapay_khqr"),
    CASH_ON_DELIVERY(null);

    private final String abaPaymentOption;

    public boolean isOnlinePayment() {
        return this.abaPaymentOption != null;
    }
}
