package com.example.commondata.domain.aggregate.valueobject;

import java.util.UUID;

public class CallerId extends BaseId<UUID> {
    public CallerId(UUID value) {
        super(value);
    }
}
