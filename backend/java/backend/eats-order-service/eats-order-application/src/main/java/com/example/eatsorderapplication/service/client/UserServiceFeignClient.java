package com.example.eatsorderapplication.service.client;

import com.example.eatsorderapplication.config.FeignConfig;
import com.example.eatsorderdomain.data.dto.AddressDto;
import com.example.eatsorderdomain.data.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "user", url = "${feign-client.user-service.url}", configuration = FeignConfig.class)
public interface UserServiceFeignClient {
    @GetMapping("/account/locations")
    List<AddressDto> findUserLatLonByIds(@RequestParam List<UUID> userIds);
}