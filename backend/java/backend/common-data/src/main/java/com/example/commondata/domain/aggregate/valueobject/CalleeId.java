package com.example.commondata.domain.aggregate.valueobject;

import java.util.UUID;

public class CalleeId extends BaseId<UUID> {
    public CalleeId(UUID value) {
        super(value);
    }
}
