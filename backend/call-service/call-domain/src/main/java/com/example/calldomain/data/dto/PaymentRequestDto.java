package com.example.calldomain.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentRequestDto {
    private String type;
    private String orderId;
    private String amount;
    private String paymentKey;
}
