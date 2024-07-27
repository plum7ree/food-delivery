package com.example.eatsorderdomain.data.domainentity;

import com.example.commondata.domain.aggregate.entity.BaseEntity;
import com.example.commondata.domain.aggregate.valueobject.SimpleId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class RestaurantMenuOption extends BaseEntity<SimpleId> {
    private String name;
    private BigInteger cost;
}