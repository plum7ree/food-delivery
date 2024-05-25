package com.example.eatsorderdomain.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RequestDto {
    private String type;
    private String orderId;
    private String amount;
    private String paymentKey;
}
