package com.example.user.data.dto;

import com.example.user.data.entity.Option;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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
}
