package com.example.commondata.domain.aggregate;

import com.example.commondata.domain.aggregate.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
public abstract class AggregateRoot<ID> extends BaseEntity<ID> {
}