package com.example.driver.controller;

import com.example.driver.dto.LocationDto;
import com.example.driver.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LocationController {

//    private KafkaTemplate<Long, Object> kafkaProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    private final RedissonReactiveClient redissonReactiveClient;

    @Value("${driver.redis.keys.driver-location-edge}")
    private String driverEdgeHashKey;
     @Value("${driver.redis.keys.edge-visit}")
    private String edgeRankingKey;

    //TODO userId from security utils
    // option 1. @AuthenticationPrincipal UserDetails userDetails
    // option 2. Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //           String userId = authentication.getName();

    @PostMapping("/api/driver/location")
    public Mono<ResponseEntity<ResponseDto>> monoExample(@RequestBody LocationDto locationDto) {

        String driverId = locationDto.getDriverId(); // Assuming LocationDto contains a driver ID.
        String oldEdgeId = locationDto.getOldEdgeId();
        String currEdgeId = locationDto.getEdgeId();


        String script = "local driverId = KEYS[1]\n" +
                "local oldEdgeId = KEYS[2]  -- This might be 'none' if not provided\n" +
                "local currEdgeId = KEYS[3]\n" +
                "local driverInfo = redis.call('hget', '" + driverEdgeHashKey + "', driverId)\n" +
                "local map = '" + edgeRankingKey + "'\n" +
                "\n" +
                "-- Only proceed with oldEdgeId operations if oldEdgeId is not 'none'\n" +
                "if oldEdgeId ~= 'none' then\n" +
                "    local oldEdgeValue = redis.call('hget', map, oldEdgeId)\n" +
                "    if oldEdgeValue then\n" +
                "        oldEdgeValue = oldEdgeValue - 1\n" +
                "        redis.call('hset', map, oldEdgeId, oldEdgeValue)\n" +
                "    else\n" +
                "        redis.call('hset', map, oldEdgeId, -1)\n" +
                "    end\n" +
                "end\n" +
                "\n" +
                "-- Operations for currEdgeId proceed as usual\n" +
                "local currEdgeValue = redis.call('hget', map, currEdgeId)\n" +
                "if currEdgeValue then\n" +
                "    currEdgeValue = currEdgeValue + 1\n" +
                "    redis.call('hset', map, currEdgeId, currEdgeValue)\n" +
                "else\n" +
                "    redis.call('hset', map, currEdgeId, 1)\n" +
                "end\n" +
                "\n" +
                "-- Return values based on whether oldEdgeId was provided\n" +
                "if oldEdgeId ~= 'none' then\n" +
                "    return {oldEdgeValue, currEdgeValue}\n" +
                "else\n" +
                "    return {nil, currEdgeValue}\n" +
                "end";



        // Keys that the script will operate on
        List<Object> keys = Arrays.asList(driverId, (oldEdgeId != null ? oldEdgeId : "none"), currEdgeId);

        // Execute the script
        Mono<Object> results = redissonReactiveClient.getScript().eval(RScript.Mode.READ_WRITE,
                                                                script,
                                                                RScript.ReturnType.MULTI,
                                                                keys);

//        results.block() or subscribe() // non-block
//        doOnSuccess or doOnNext or doOnError
        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("200", "")));

    }

}