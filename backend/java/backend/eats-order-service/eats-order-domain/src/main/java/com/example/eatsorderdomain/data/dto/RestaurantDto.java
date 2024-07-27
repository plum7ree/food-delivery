package com.example.eatsorderdomain.data.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * 받는 측의 필드는 몇개가 누락되도 상관없다.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {
    UUID id;
    String sessionId;
    String userId;
    String name;

    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    LocalTime openTime;
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    LocalTime closeTime;


}
