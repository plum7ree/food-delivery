package com.example.eatsorderdomain.data.dto;


import com.example.commondata.domain.aggregate.valueobject.Address;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class CreateEatsOrderCommandDto {
    @NotNull
    private final UUID userId;
    @NotNull
    private final UUID driverId;
    @NotNull
    private final BigDecimal price; //TODO how to add info of currency? won, dollars...
    @NotNull
    private final Address address;

    private final RequestDto payment;

    private final RouteRequestDto route;
}
