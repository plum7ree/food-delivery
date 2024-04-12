package com.example.route.controller;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.example.route.data.dto.AddressSearchRequestDto;
import com.example.route.data.dto.InstructionDto;
import com.example.route.data.dto.PointDto;
import com.example.route.data.dto.RouteResponseDto;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.shapes.GHPoint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class RouteController {
    private static final Logger log = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private GraphHopper graphHopper;

    @Autowired
    private ElasticsearchClient elasticsearchClient;
        private static final String INDEX_NAME = "address";


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
    public void searchAddress(@RequestParam String text) {
        MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(q -> q
                .query(text)
                .fields(List.of("city_county_district", "eup_myeon_dong", "ri", "road_name"))
                .fuzziness("1")
                .prefixLength(2)
        )._toQuery().multiMatch();

        Query queryDsl = Query.of(q -> q
                .multiMatch(multiMatchQuery)
        ).

        SearchRequest searchRequest = SearchRequest.of(r -> r
                .index(INDEX_NAME)
                .query(queryDsl)
                .from(0)
                .size(10)
        );

        SearchResponse<JsonData> searchResponse = elasticsearchClient.search(searchRequest, JsonData.class);
        return searchResponse.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();



String searchText = "bike";

SearchResponse<Product> response = esClient.search(s -> s
    .index("products")
    .query(q -> q
        .match(t -> t
            .field("name")
            .query(searchText)
        )
    ),
    Product.class
);

TotalHits total = response.hits().total();
boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

if (isExactResult) {
    logger.info("There are " + total.value() + " results");
} else {
    logger.info("There are more than " + total.value() + " results");
}

List<Hit<Product>> hits = response.hits().hits();
for (Hit<Product> hit: hits) {
    Product product = hit.source();
    logger.info("Found product " + product.getSku() + ", score " + hit.score());
}


    }

     @GetMapping("/address/search")
    public void getOsmId(@RequestParam String text) {

}
