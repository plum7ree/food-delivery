package com.example.commondata.domain.aggregate.valueobject;

import java.util.UUID;

public class CallId extends BaseId<UUID> {
    public CallId(UUID value) {
        super(value);
    }
}
