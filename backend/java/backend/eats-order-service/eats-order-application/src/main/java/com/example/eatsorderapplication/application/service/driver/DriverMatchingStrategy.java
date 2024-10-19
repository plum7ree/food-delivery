package com.example.eatsorderapplication.application.service.driver;

import com.example.commondata.dto.order.UserOrderAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface DriverMatchingStrategy {
    /**
     * candidates 는 driver dto 와 user address dto 의 조합으로 graph 에서 edge 에 해당할 수 있다.
     * 다만, user1-driver1, user1-driver2, user2-driver1, 등이 있으므로,
     * user address dto 와 driver dto 는 set 에 넣어도 동일한 dto 인지 판단이 가능하도록
     * equals 와 hash 함수가 존재해야 한다.
     * TODO AddressDto 를 Domain Object 로 바꾸자.
     * @param candidates
     * @return
     */
    Mono<List<Matching>> match(Set<Candidate> candidates);
}
