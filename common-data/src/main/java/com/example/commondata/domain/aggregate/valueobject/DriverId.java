package com.example.commondata.domain.aggregate.valueobject;

import java.util.UUID;

public class DriverId extends BaseId<UUID> {
    public DriverId(UUID value) {
        super(value);
    }
}
