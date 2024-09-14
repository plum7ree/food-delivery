package com.example.eatsorderdomain.data.domainentity;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class OrderItem {
    @NotNull
    private UUID id;
    @NotNull
    private UUID orderId;
    @NotNull
    private Integer quantity;
    @NotNull
    private Double price;
    @NotNull
    private Double subTotal;


}
