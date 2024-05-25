package com.example.user.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDto {
    @Builder.Default
    List<OptionGroupDto> optionGroupDtoList = new ArrayList<>();
    @JsonIgnore
    private UUID id;
    private UUID restaurantId;
    private String name;
    private String description;
    private BigInteger price;
    private String pictureUrl;
}
