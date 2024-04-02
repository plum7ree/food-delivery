package com.example.commondata.domain.aggregate.valueobject;

import java.util.UUID;

/**
 * getValue will return UUID
 */
public class DriverApprovalId extends BaseId<UUID> {
    public DriverApprovalId(UUID value) {
        super(value);
    }
}
