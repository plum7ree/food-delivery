package com.example.calldomain.data.dto;


import com.example.commondata.domain.aggregate.valueobject.Address;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CreateCallCommandDto {
    @NotNull
    private final UUID userId;
    @NotNull
    private final UUID driverId;
    @NotNull
    private final BigDecimal price; //TODO how to add info of currency? won, dollars...
    @NotNull
    private final Address address;
}
