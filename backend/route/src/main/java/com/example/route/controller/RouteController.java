package com.example.route.controller;
import brave.Response;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.example.route.data.dto.*;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.shapes.GHPoint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Slf4j
public class RouteController {

    @Autowired
    private GraphHopper graphHopper;

    @Autowired
    private ElasticsearchClient esClient;
    private static final String INDEX_NAME = "osm";


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
        // https://github.com/graphhopper/graphhopper/blob/master/docs/web/api-doc.md
        log.info("coord: " + startLat + " " + startLon + " " + destLat + " " + destLon + " ");
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

    @GetMapping("/address/search")
    public ResponseEntity<OsmDto> searchAddress(@RequestParam String text) throws IOException {

        SearchResponse<OsmDto> response = esClient.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .multiMatch(t -> t
                    .fields("name", "street", "city")
                    .query(text)
                )
            ),
            OsmDto.class
        );


        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            log.info("There are " + total.value() + " results");
        } else {
            log.info("There are more than " + total.value() + " results");
        }

        List<Hit<OsmDto>> hits = response.hits().hits();
        for (Hit<OsmDto> hit: hits) {
            OsmDto osmDto = hit.source();
            log.info("Found product " + osmDto.toString() + ", score " + hit.score());
        }
        if (hits.size() > 0) {
            var bestScoreOsmDto = hits.get(0).source();
            return ResponseEntity.ok(OsmDto.builder()
                    .name(bestScoreOsmDto.getName())
                    .city(bestScoreOsmDto.getCity())
                    .street(bestScoreOsmDto.getStreet())
                    .osmid(bestScoreOsmDto.getOsmid())
                    .lat(bestScoreOsmDto.getLat())
                    .lon(bestScoreOsmDto.getLon()).build());

        }
        return ResponseEntity.status(400).body(null);

    }


}
