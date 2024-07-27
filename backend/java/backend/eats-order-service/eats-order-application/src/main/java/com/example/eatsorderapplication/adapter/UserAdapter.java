//package com.example.eatsorderapplication.adapter;
//
//import com.example.eatsorderapplication.data.dto.UserDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Objects;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class UserAdapter {
//
//    private final RestTemplate restTemplate;
//
//    @Value("${user.service.url}")
//    private String userServiceUrl;
//
//
//    public Optional<UserDto> findByEmail(String email) {
//
//        HttpHeaders userServiceHeaders = new HttpHeaders();
//        userServiceHeaders.set("X-Auth-User-Email", email);
//        HttpEntity<?> requestEntity = new HttpEntity<>(userServiceHeaders);
//        ResponseEntity<UserDto> userResponse = restTemplate.exchange(
//            userServiceUrl,
//            HttpMethod.GET,
//            requestEntity,
//            UserDto.class
//        );
//
//        if(userResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
//            return Optional.empty();
//        }
//        return Optional.ofNullable(userResponse.getBody());
//    }
//
//}
