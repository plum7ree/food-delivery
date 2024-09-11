package com.example.eatsorderapplication.service.driver;

import com.example.eatsorderdomain.data.dto.AddressDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DriverMatchingStrategy {
    // TODO AddressDto 를 Domain Object 로 바꾸자.
    Mono<List<Matching>> findNearbyDrivers(List<AddressDto> userLocations);

}
