package com.example.driver.data.transformer;


import com.example.driver.data.dto.LocationDto;
import com.example.kafka.avro.model.Coordinates;
import com.example.kafka.avro.model.LocationAvroModel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class LocationToAvroTransformer {

    //TODO what if several threads call this concurrently?
    public LocationAvroModel transform(LocationDto locationDto) {
        return LocationAvroModel
                .newBuilder()
                .setDriverId(locationDto.getDriverId())
                .setOldEdgeId(locationDto.getOldEdgeId())
                .setEdgeId(locationDto.getEdgeId())
                .setCoord(Coordinates.newBuilder().setLat(locationDto.getLat()).setLon(locationDto.getLon()).build())
                .setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .build();
    }
}
