package com.example.commondata.domain.aggregate.valueobject;

import java.util.UUID;

public class SimpleId extends BaseId<UUID> {
    public SimpleId(UUID value) {
        super(value);
    }
}
