package com.example.user.data.dto;

import com.example.user.data.entity.OptionGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDto {
    @JsonIgnore
    private String id;

    private String name;
    private String description;
    private String price;

    private String pictureUrl;
        @Builder.Default
    List<OptionGroupDto> optionGroupDtoList = new ArrayList<>();
}
