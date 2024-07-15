package com.example.eatssearch.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructionDto {
    Integer sign;
    String name;
    Double distance;
    Long time;
}
