package com.liang.order.dto;

import com.liang.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "Delivery address is required")
    private Long addressId;

    @NotNull(message = "Payment method is required")
    @Pattern(regexp = "^(?i)(ABA_PAYWAY_KHQR|CASH_ON_DELIVERY)$", message = "Payment method must be ABA_PAYWAY_KHQR or CASH_ON_DELIVERY")
    private String paymentMethod;

    private String note;
}
