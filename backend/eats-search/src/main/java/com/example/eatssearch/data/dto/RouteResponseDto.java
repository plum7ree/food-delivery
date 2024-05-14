package com.example.eatssearch.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponseDto {
    List<PointDto> pointList;
    List<InstructionDto> instructionList;
}
