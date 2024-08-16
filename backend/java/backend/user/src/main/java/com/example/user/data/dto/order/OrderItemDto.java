package com.example.user.data.dto.order;

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
public class OrderItemDto {
    @NotNull
    private UUID id;
    @NotNull
    private Integer quantity;
    @NotNull
    private BigDecimal price;

    private List<OptionDto> optionDtoList;

}
