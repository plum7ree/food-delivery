package com.example.eatsorderdomain.data.dto;


import com.example.commondata.domain.aggregate.valueobject.Address;
import com.example.eatsorderdomain.data.dto.OrderItemDto;
import com.example.eatsorderdomain.data.dto.PaymentDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderCommandDto {
    @NotNull
    @JsonProperty
    private UUID callerId; //TODO remove this.
    @NotNull
    @JsonProperty
    private UUID calleeId;
    @NotNull
    @JsonProperty
    private BigDecimal price; //TODO how to add info of currency? won, dollars...
    @NotNull
    @JsonProperty
    private Address address;
    @NotNull
    @JsonProperty
    private PaymentDto payment;
    @NotNull
    @JsonProperty
    private List<OrderItemDto> items;

    public void validate() {
    }
}
