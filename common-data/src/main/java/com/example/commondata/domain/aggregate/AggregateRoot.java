package com.example.commondata.domain.aggregate;

import com.example.commondata.domain.aggregate.entity.BaseEntity;
import com.example.commondata.domain.aggregate.valueobject.PaymentId;

public abstract class AggregateRoot<ID> extends BaseEntity<ID> {
}
