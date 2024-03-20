package com.example.locationredis.service;

import com.example.driver.dto.LocationDto;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScript;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RedisService {
    private static final Logger log = LoggerFactory.getLogger(RedisService.class);

    private final RedissonReactiveClient redissonReactiveClient;


    @Value("${redis.driver-location.key}")
    private String driverLocationKey;
    @Value("${redis.driver-location.fields.location.edge}")
    private String locationEdgeFieldName;

    @Value("${redis.edge-count.key}")
    private String edgeCountKey;
    @Value("${redis.edge-count.fields.count}")
    private String countFieldName;


    //TODO pipeline
    public void save(LocationDto locationDto) {

        log.info("location Dto Received: " + locationDto.toString());
        String driverId = locationDto.getDriverId(); // Assuming LocationDto contains a driver ID.
        String oldEdgeId = locationDto.getOldEdgeId();
        String currEdgeId = locationDto.getEdgeId();


        ResourceScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("META-INF/scripts/location-edge-update.lua"));

        // Keys that the script will operate on
        List<Object> keys = Arrays.asList(String.format(driverLocationKey, driverId), String.format(edgeCountKey, currEdgeId), String.format(edgeCountKey, oldEdgeId));
        List<Object> args = Arrays.asList(locationEdgeFieldName, countFieldName, currEdgeId);


        log.info("keys: " + keys + " args: " + args);

        try {
            // keys, args List<Object> 으로 해야하고,
            // StringCodec.INSTANCE 반드시 코덱 설정해줘야한다.
            // Object...values 부분을 args 로 값을 주면 args 를 하나의 Object 로 인식해버린다!
            // 따라서 args.toArray() 혹은 args.toArray(new Object[0]) 로 넘겨준다
            // ref: https://stackoverflow.com/questions/9863742/how-to-pass-an-arraylist-to-a-varargs-method-parameter
            Mono<Object> results = redissonReactiveClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE,
                    scriptSource.getScriptAsString(),
                    RScript.ReturnType.BOOLEAN,
                    keys, args.toArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
