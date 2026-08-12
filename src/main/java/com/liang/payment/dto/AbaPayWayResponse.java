package com.liang.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbaPayWayResponse {

    private Long orderId;
    private String tranId;
    private BigDecimal amount;
    private String currency;
    private String qrString;
    private String qrImage;
    private String abaPayDeeplink;
    private String statusCode;
    private String statusMessage;

    private LocalDateTime expiresAt;
}
