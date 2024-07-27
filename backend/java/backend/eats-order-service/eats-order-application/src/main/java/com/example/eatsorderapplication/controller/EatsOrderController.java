package com.example.eatsorderapplication.controller;

import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderapplication.service.EatsOrderCommandService;
import com.example.eatsorderdomain.data.dto.CreateOrderCommandDto;
import com.example.eatsorderdomain.data.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class EatsOrderController {
    private static final Logger log = LoggerFactory.getLogger(EatsOrderController.class);

    @Autowired
    private final EatsOrderCommandService eatsOrderCommandService;

    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;


    /**
     * @param headers
     * @param createOrderCommandDto
     * @return
     */
    @PostMapping("/eatsorder")
    public ResponseEntity<EatsOrderResponseDto> createOrder(@RequestHeader HttpHeaders headers, @RequestBody CreateOrderCommandDto createOrderCommandDto) {
        try {
            // User Athentication
//            var email = Objects.requireNonNull(headers.get("X-Auth-User-Email")).get(0);
//
//            HttpHeaders userServiceHeaders = new HttpHeaders();
//            userServiceHeaders.set("X-Auth-User-Email", email);
//            HttpEntity<?> requestEntity = new HttpEntity<>(userServiceHeaders);
//
//            ResponseEntity<UserDto> userResponse = restTemplate.exchange(
//                userServiceUrl,
//                HttpMethod.GET,
//                requestEntity,
//                UserDto.class
//            );
//
//            if(userResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
//                return ResponseEntity.badRequest().build();
//            }
//            UserDto userDto = userResponse.getBody();
//
//            createOrderCommandDto.setCallerId(UUID.fromString(userDto.getId()));

            var response = eatsOrderCommandService.createAndSaveOrder(createOrderCommandDto);
            return ResponseEntity.ok(response);
//            return ResponseEntity.ok(null);
        } catch (Exception e) {
            log.error(e.toString());
            var response = EatsOrderResponseDto.builder().message("internal error occured").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/cancelorder")
    public ResponseEntity<EatsOrderResponseDto> cancelOrder(@RequestHeader HttpHeaders headers, @RequestBody CreateOrderCommandDto createOrderCommandDto) {
        try {
            var email = Objects.requireNonNull(headers.get("X-Auth-User-Email")).get(0);

            HttpHeaders userServiceHeaders = new HttpHeaders();
            userServiceHeaders.set("X-Auth-User-Email", email);
            HttpEntity<?> requestEntity = new HttpEntity<>(userServiceHeaders);

            ResponseEntity<UserDto> userResponse = restTemplate.exchange(
                userServiceUrl,
                HttpMethod.GET,
                requestEntity,
                UserDto.class
            );

            UserDto userDto = userResponse.getBody();
            if (userDto == null) {
                userDto = new UserDto(); // 또는 적절한 기본값 설정
            }

            createOrderCommandDto.setCallerId(UUID.fromString(userDto.getId()));

            var response = eatsOrderCommandService.createAndSaveOrder(createOrderCommandDto);
            return ResponseEntity.ok(response);
//            return ResponseEntity.ok(null);
        } catch (Exception e) {
            var response = EatsOrderResponseDto.builder().message("internal error occured").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PostMapping("/test")
    public ResponseEntity<String> testPost() {

        return ResponseEntity.ok("done");


    }
//    @GetMapping("/fetch")
//    public ResponseEntity<Call> fetchMostRecentCallStatus(@RequestParam UUID userId) {
//        var callEntityList = callRepository.findCallEntitiesByUserId(userId, 1);
//
//        return ResponseEntity.ok(callEntityList.get(0));
//    }


}
