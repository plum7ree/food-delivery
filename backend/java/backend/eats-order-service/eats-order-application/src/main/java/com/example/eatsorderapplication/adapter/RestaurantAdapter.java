package com.example.eatsorderapplication.adapter;

import com.example.eatsorderdomain.data.dto.RestaurantDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

/**
 * Restaurant 에 요청하는 방식 2가지
 * 1. 이런식으로 RestTemplate 으로 user service 에 요청
 * 2. database 자체를 readonly 방식으로 복제해서 직접 repository 만들어서 요청
 */
@Component
@Slf4j
public class RestaurantAdapter {

    private final RestTemplate restTemplate;

    @Value("${user.restaurant-info.url}")
    private String restaurantInfoUrl;

    public RestaurantAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * core: wants price and quantity
     * send restaurantId and open/close time and other status
     *
     * @param id
     * @return
     */
    public Optional<RestaurantDto> findById(UUID id) {

        HttpHeaders userServiceHeaders = new HttpHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(id.toString(), userServiceHeaders);

        log.info("restaurant info url: {}", restaurantInfoUrl + "/" + id.toString());
        // send restaurant info
        ResponseEntity<RestaurantDto> userResponse = restTemplate.exchange(
            restaurantInfoUrl + "/" + id.toString(),
            HttpMethod.GET,
            requestEntity,
            RestaurantDto.class
        );


        if (userResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return Optional.empty();
        }
        return Optional.ofNullable(userResponse.getBody());
    }


}
