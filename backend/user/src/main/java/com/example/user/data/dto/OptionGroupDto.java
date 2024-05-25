package com.example.user.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionGroupDto {
    String description;
    @Builder.Default
    Integer maxSelectNumber = 1;
    @Builder.Default
    boolean isNecessary = false;
    @Builder.Default
    List<OptionDto> selectedIndicesList = new ArrayList<>();
    @Builder.Default
    List<OptionDto> optionDtoList = new ArrayList<>();
    @JsonIgnore
    private UUID id;
}
