package com.example.driver.controller;

import com.example.driver.dto.LocationDto;
import com.example.driver.dto.ResponseDto;
import com.example.driver.transformer.LocationToAvroTransformer;
import com.example.kafka.config.data.KafkaConfigData;
import com.example.kafkaproducer.KafkaProducer;
import com.microservices.demo.kafka.avro.model.LocationAvroModel;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

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


    @Operation(
            summary = "update location",
            description = "POST {gateway-server-url}/driver/location/api/update"
    )
    @PostMapping("/update")
    public Mono<ResponseEntity<ResponseDto>> monoExample(@RequestBody LocationDto locationDto) throws IOException {
        //TODO key based on userId or user location?
        kafkaProducer.send(kafkaConfigData.getTopicName(), Long.parseLong(locationDto.getDriverId()), locationToAvroTransformer.transform(locationDto));
//
//
//        log.info("location Dto Received: " + locationDto.toString());
//        String driverId = locationDto.getDriverId(); // Assuming LocationDto contains a driver ID.
//        String oldEdgeId = locationDto.getOldEdgeId();
//        String currEdgeId = locationDto.getEdgeId();
//
//
//
//
//        ResourceScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("META-INF/scripts/location-edge-update.lua"));
//
//        // Keys that the script will operate on
//        List<Object> keys = Arrays.asList(String.format(driverLocationKey,driverId), String.format(edgeCountKey, currEdgeId), String.format(edgeCountKey,oldEdgeId));
//        List<Object> args = Arrays.asList(locationEdgeFieldName, countFieldName, currEdgeId);
//
//
//        //List<Object> keys = Arrays.asList(String.format("driver:%s", driverId),
////                                  String.format("edge:%s", currEdgeId),
////                                  String.format("edge:%s", oldEdgeId));
////
////List<Object> args = Arrays.asList(locationEdgeFieldName, edgeVisitFieldName, driverId, currEdgeId, oldEdgeId);
//
//
//        log.info("keys: " + keys.toString() + " args: " + args.toString());
//
//        // keys, args List<Object> 으로 해야하고,
//        // StringCodec.INSTANCE 반드시 코덱 설정해줘야한다.
//        // Object...values 부분을 args 로 값을 주면 args 를 하나의 Object 로 인식해버린다!
//        // 따라서 args.toArray() 혹은 args.toArray(new Object[0]) 로 넘겨준다
//        // ref: https://stackoverflow.com/questions/9863742/how-to-pass-an-arraylist-to-a-varargs-method-parameter
//        Mono<Object> results = redissonReactiveClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE,
//                                                                scriptSource.getScriptAsString(),
//                                                                RScript.ReturnType.BOOLEAN,
//                                                                keys, args.toArray());


//        results.block() or subscribe() // non-block
//        doOnSuccess or doOnNext or doOnError
        return Mono.just(ResponseEntity.ok(new ResponseDto("200", "")));
    }

}