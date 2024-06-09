package com.example.commondata.domain.aggregate.valueobject;

import java.util.UUID;

/**
 * getValue will return UUID
 */
public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
