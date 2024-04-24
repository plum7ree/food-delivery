package com.example.user.data.dto;

import com.example.user.data.entity.OptionGroup;
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
public class MenuDto {

    private String name;
    private String description;
    private String price;

    private String pictureUrl;
    List<OptionGroupDto> optionGroupDtoList;
}
