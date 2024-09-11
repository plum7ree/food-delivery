package com.example.user.data.dto.order;


import com.example.user.data.dto.AddressDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private AddressDto address;
    @NotNull
    @JsonProperty
    private PaymentDto payment;
    @NotNull
    @JsonProperty
    private List<OrderItemDto> items;

    @NotNull
    @JsonProperty
    private UUID orderId;

    public void validate() {
    }
}
