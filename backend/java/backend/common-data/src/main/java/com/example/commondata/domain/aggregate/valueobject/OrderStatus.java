package com.example.commondata.domain.aggregate.valueobject;

public enum OrderStatus {
    PENDING,
    CALLER_CANCELLED,
    PAYMENT_COMPLETED,
    PAYMENT_CANCELLED,
    CALLEE_APPROVED,
    CALLEE_REJECTED

}