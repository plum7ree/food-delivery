package com.example.driver.controller;

import com.example.driver.dto.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class LocationController {

    private KafkaTemplate<Long, Object> kafkaProducer;
    private RedisTemplate<String, Object> redisTemplate;

    //TODO userId from security utils
    // option 1. @AuthenticationPrincipal UserDetails userDetails
    // option 2. Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //           String userId = authentication.getName();

//    @PostMapping("/api/driver/location")
//    public Mono<String> monoExample(@RequestBody LocationDto locationDto) {
//        // where was former edge?
//
//        // if a driver entered into new edge, decrement the old one and increment the new one.
//
//        redisTemplate.opsForZSet().incrementScore("${driver.edge-ranking.key.name}}", locationDto.getEdgeId(), 1);
////        kafkaProducer.send()
//        return Mono.just("Mono with value: " + value);

//    String driverId = locationDto.getDriverId(); // Assuming LocationDto contains a driver ID.
//    String newEdgeId = locationDto.getEdgeId();
//    String driverEdgeHashKey = "driver:edge"; // Redis hash key to store driver's current edge
//    String edgeRankingKey = "driver.edge-ranking.key.name"; // Redis ZSet key for edge ranking
//
//    return Mono.just(redisTemplate.opsForHash().get(driverEdgeHashKey, driverId))
//        .flatMap(oldEdgeId -> {
//            Mono<Void> decrementOldEdgeMono = Mono.empty();
//            if (oldEdgeId != null && !oldEdgeId.equals(newEdgeId)) {
//                // Decrement score for the old edge
//                decrementOldEdgeMono = Mono.fromCallable(() -> redisTemplate.opsForZSet().incrementScore(edgeRankingKey, oldEdgeId, -1)).then();
//            }
//            return decrementOldEdgeMono.thenReturn(oldEdgeId);
//        })
//        .flatMap(oldEdgeId -> {
//            // Increment score for the new edge
//            redisTemplate.opsForZSet().incrementScore(edgeRankingKey, newEdgeId, 1);
//            // Update the driver's current edge in the hash
//            redisTemplate.opsForHash().put(driverEdgeHashKey, driverId, newEdgeId);
//            // Send a message to Kafka, if needed
//            // kafkaProducer.send(...)
//            return Mono.just("Edge updated from " + oldEdgeId + " to " + newEdgeId);
//        });


//    }

    @GetMapping("/flux")
    public Flux<String> fluxExample() {
        return Flux.just("Value 1", "Value 2", "Value 3");
    }
}