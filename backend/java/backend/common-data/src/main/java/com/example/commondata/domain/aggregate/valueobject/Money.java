package com.example.commondata.domain.aggregate.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Money {
    @JsonProperty
    private BigDecimal amount;


}
