package com.example.driver.controller;

import com.example.driver.data.dto.LocationDto;
import com.example.driver.data.dto.ResponseDto;
import com.example.driver.data.transformer.LocationToAvroTransformer;
import com.example.kafka.avro.model.LocationAvroModel;
import com.example.kafka.config.data.KafkaConfigData;
import com.example.kafkaproducer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.UUID;

@RestController
// {gateway-server-url}/driver/location/api/...
@RequestMapping(path = "/location/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class LocationController {
    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    private final KafkaProducer<Long, LocationAvroModel> kafkaProducer;
    // figured in configserver's driver.yml
    private final KafkaConfigData kafkaConfigData;

    private final LocationToAvroTransformer locationToAvroTransformer;


    //TODO userId from security utils
    // option 1. @AuthenticationPrincipal UserDetails userDetails
    // option 2. Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //           String userId = authentication.getName();
    //TODO
    // 여기서 바로 레디스로 업데이트 하지말고, kafka 로 보내자. 해당 이벤트를 레디스와 나의 HashMap, TreeSet 이 있는 서비스에서 구독.
    // 주기적으로 ranking TreeSet 업데이트.

    // 주의. schema registered 가 잘 되었는가?

    @PostMapping("/update")
    public Mono<ResponseEntity<ResponseDto>> monoExample(@RequestBody LocationDto locationDto) throws IOException {
        //TODO key based on userId or user location?
        log.info("location update, sending to kafka. topic name: "
                + kafkaConfigData.getTopicName()
                + " key: " + locationDto.getDriverId());
        // Kafka Key should be Long.
        // UUID -> Long. there might be a info loss.
        // ref: https://www.baeldung.com/java-uuid-unique-long-generation
        String driverId = locationDto.getDriverId().toString();
        long kafkaKey = UUID.fromString(driverId).getLeastSignificantBits();
        kafkaProducer.send(kafkaConfigData.getTopicName(), kafkaKey, locationToAvroTransformer.transform(locationDto));

//        results.block() or subscribe() // non-block
//        doOnSuccess or doOnNext or doOnError
        return Mono.just(ResponseEntity.ok(new ResponseDto("200", "")));
    }

}