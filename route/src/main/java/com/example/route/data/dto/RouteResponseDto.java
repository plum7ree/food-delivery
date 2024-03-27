package com.example.route.data.dto;

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
