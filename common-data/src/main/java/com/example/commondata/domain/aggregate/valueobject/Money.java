package com.example.commondata.domain.aggregate.valueobject;

import java.math.BigDecimal;

public class Money {
    private final BigDecimal amount;
    public Money(BigDecimal amount) {
        this.amount = amount;
    }
}
