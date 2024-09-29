package com.example.eatsorderapplication.application.service.driver;

import com.example.commondata.dto.order.UserOrderAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface DriverMatchingStrategy {
    // TODO AddressDto 를 Domain Object 로 바꾸자.
    Mono<List<Matching>> match(Set<UserOrderAddressDto> users, Set<DriverDetailsDto> drivers);
}
