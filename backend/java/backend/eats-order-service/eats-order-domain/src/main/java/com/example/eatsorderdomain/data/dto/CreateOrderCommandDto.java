package com.example.eatsorderdomain.data.dto;


import com.example.commondata.domain.aggregate.valueobject.Address;
import com.example.eatsorderdomain.data.aggregate.OrderItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class CreateOrderCommandDto {
    @NotNull
    @JsonProperty
    private final UUID callerId;
    @NotNull
    @JsonProperty
    private final UUID calleeId;
    @NotNull
    @JsonProperty
    private final BigDecimal price; //TODO how to add info of currency? won, dollars...
    @NotNull
    @JsonProperty
    private final Address address;
    @JsonProperty
    private final RequestDto payment;
    @JsonProperty
    private final List<OrderItem> items;
}
