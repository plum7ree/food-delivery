package com.example.eatsorderapplication.application.service.driver;

import com.example.commondata.dto.order.AddressDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DriverMatchingStrategy {
    // TODO AddressDto 를 Domain Object 로 바꾸자.
    Mono<List<Matching>> findNearbyDrivers(List<AddressDto> userLocations);

}
