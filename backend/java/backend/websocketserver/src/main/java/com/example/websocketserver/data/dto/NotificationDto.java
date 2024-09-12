package com.example.websocketserver.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY) // 빈 필드는 JSON에서 제외

public class NotificationDto {

    String type;
    DriverDetailsDto driverDetails;
    String message;

}
