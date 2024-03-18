package com.example.route.controller;

import com.example.route.data.dto.*;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.shapes.GHPoint;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(path="/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class RouteController {
    private static final Logger log = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private GraphHopper graphHopper;

    @Operation(
            summary = "routing request"
    )
//    @ApiResponse({
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "HTTP status Query successful"
//            )
//    })
    @GetMapping("/query")
    public Mono<ResponseEntity<RouteResponseDto>> query(@RequestParam Double startLat,
                                                        @RequestParam Double startLon,
                                                        @RequestParam Double destLat,
                                                        @RequestParam Double destLon
    ) {
        log.info("coord: " + startLat + " "+ startLon + " "+ destLat + " "+ destLon + " ");
        GHPoint startPoint = new GHPoint();
        startPoint.lat = startLat;
        startPoint.lon = startLon;

        GHPoint endPoint = new GHPoint();
        endPoint.lat = destLat;
        endPoint.lon = destLon;

        GHRequest req = new GHRequest(startPoint, endPoint).setProfile("car").setLocale(Locale.US);
        GHResponse res = graphHopper.route(req);

        var bestPath = res.getBest();
        var points = bestPath.getPoints();
        var instructions = bestPath.getInstructions();

        var pointDtoList = StreamSupport.stream(Spliterators.spliteratorUnknownSize(points.iterator(), 0), false)
                .map(point -> new PointDto(point.getLat(), point.getLon()))
                .collect(Collectors.toList());

        var instructionDtoList = StreamSupport.stream(Spliterators.spliteratorUnknownSize(instructions.iterator(), 0), false)
                .map(instruction -> new InstructionDto(instruction.getSign(), instruction.getName(), instruction.getDistance(), instruction.getTime()))
                .collect(Collectors.toList());

        return Mono.just(ResponseEntity.ok(new RouteResponseDto(pointDtoList, instructionDtoList)));


//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
    }

    @PostMapping("/address/search")
    public void searchAddress(AddressSearchRequestDto addressSearchRequestDto) {
        //TODO. elastic search
    }

}
