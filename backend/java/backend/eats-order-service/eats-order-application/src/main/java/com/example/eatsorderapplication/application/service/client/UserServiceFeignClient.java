package com.example.eatsorderapplication.application.service.client;

import com.example.commondata.dto.order.AddressDto;
import com.example.eatsorderapplication.application.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user", url = "${feign-client.user-service.url}", configuration = FeignConfig.class)
public interface UserServiceFeignClient {
    @GetMapping("/account/locations")
    List<AddressDto> findUserLatLonByIds(@RequestParam List<UUID> userIds);
}