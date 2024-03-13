package com.example.driver.controller;

import com.example.driver.dto.LocationDto;
import com.example.driver.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LocationController {
    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

//    private KafkaTemplate<Long, Object> kafkaProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    private final RedissonReactiveClient redissonReactiveClient;

    @Value("${driver.redis.fields.location-edge.key}")
    private String locationEdgeKey;

    @Value("${driver.redis.fields.location-edge.field-name}")
    private String locationEdgeFieldName;

     @Value("${driver.redis.fields.edge-visit.key}")
    private String edgeVisitKey;
     @Value("${driver.redis.fields.edge-visit.field-name}")
     private String edgeVisitFieldName;


    //TODO userId from security utils
    // option 1. @AuthenticationPrincipal UserDetails userDetails
    // option 2. Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //           String userId = authentication.getName();

    @PostMapping("/api/driver/location")
    public Mono<ResponseEntity<ResponseDto>> monoExample(@RequestBody LocationDto locationDto) throws IOException {
        log.info("location Dto Received: " + locationDto.toString());
        String driverId = locationDto.getDriverId(); // Assuming LocationDto contains a driver ID.
        String oldEdgeId = locationDto.getOldEdgeId();
        String currEdgeId = locationDto.getEdgeId();


//        String script = "local driverId = KEYS[1]\n" +
//                "local oldEdgeId = KEYS[2]  -- This might be 'none' if not provided\n" +
//                "local currEdgeId = KEYS[3]\n" +
//                "local driverInfo = redis.call('hget', '" + driverEdgeHashKey + "', driverId)\n" +
//                "local map = '" + edgeRankingKey + "'\n" +
//                "\n" +
//                "-- Only proceed with oldEdgeId operations if oldEdgeId is not 'none'\n" +
//                "if oldEdgeId ~= 'none' then\n" +
//                "    local oldEdgeValue = redis.call('hget', map, oldEdgeId)\n" +
//                "    if oldEdgeValue then\n" +
//                "        oldEdgeValue = oldEdgeValue - 1\n" +
//                "        redis.call('hset', map, oldEdgeId, oldEdgeValue)\n" +
//                "    else\n" +
//                "        redis.call('hset', map, oldEdgeId, -1)\n" +
//                "    end\n" +
//                "end\n" +
//                "\n" +
//                "-- Operations for currEdgeId proceed as usual\n" +
//                "local currEdgeValue = redis.call('hget', map, currEdgeId)\n" +
//                "if currEdgeValue then\n" +
//                "    currEdgeValue = currEdgeValue + 1\n" +
//                "    redis.call('hset', map, currEdgeId, currEdgeValue)\n" +
//                "else\n" +
//                "    redis.call('hset', map, currEdgeId, 1)\n" +
//                "end\n" +
//                "\n" +
//                "-- Return values based on whether oldEdgeId was provided\n" +
//                "if oldEdgeId ~= 'none' then\n" +
//                "    return {oldEdgeValue, currEdgeValue}\n" +
//                "else\n" +
//                "    return {nil, currEdgeValue}\n" +
//                "end";

        ResourceScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("META-INF/scripts/location-edge-update.lua"));

        // Keys that the script will operate on
        List<Object> keys = Arrays.asList(String.format(locationEdgeKey,driverId), String.format(edgeVisitKey, currEdgeId), String.format(edgeVisitKey,oldEdgeId));
        List<Object> args = Arrays.asList(locationEdgeFieldName, edgeVisitFieldName, driverId, currEdgeId, oldEdgeId);


        //List<Object> keys = Arrays.asList(String.format("driver:%s", driverId),
//                                  String.format("edge:%s", currEdgeId),
//                                  String.format("edge:%s", oldEdgeId));
//
//List<Object> args = Arrays.asList(locationEdgeFieldName, edgeVisitFieldName, driverId, currEdgeId, oldEdgeId);


        log.info("keys: " + keys.toString() + " args: " + args.toString());

        // keys, args List<Object> 으로 해야하고,
        // StringCodec.INSTANCE 반드시 코덱 설정해줘야한다.
        Mono<Object> results = redissonReactiveClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE,
                                                                scriptSource.getScriptAsString(),
                                                                RScript.ReturnType.BOOLEAN,
                                                                keys, args);



//        results.block() or subscribe() // non-block
//        doOnSuccess or doOnNext or doOnError
    return results
            .flatMap(result -> Mono.just(ResponseEntity.ok(new ResponseDto("200", "Success"))))
            .onErrorResume(error -> Mono.just(ResponseEntity.badRequest().body(new ResponseDto("400", "Error occurred: " + error.getMessage()))));
}

}