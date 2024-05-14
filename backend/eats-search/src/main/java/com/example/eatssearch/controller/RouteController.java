package com.example.eatssearch.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.example.route.data.dto.RestaurantSearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Slf4j
public class RouteController {

    private static final String INDEX_NAME = "restaurant";
    @Autowired
    private ElasticsearchClient esClient;


    @GetMapping("/search")
    public ResponseEntity<RestaurantSearchDto> searchAddress(@RequestParam String text) throws IOException {

        SearchResponse<RestaurantSearchDto> response = esClient.search(s -> s
                        .index(INDEX_NAME)
                        .query(q -> q
                                .multiMatch(t -> t
                                        .fields("name", "street", "city")
                                        .query(text)
                                )
                        ),
                RestaurantSearchDto.class
        );


        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            log.info("There are " + total.value() + " results");
        } else {
            log.info("There are more than " + total.value() + " results");
        }

        List<Hit<RestaurantSearchDto>> hits = response.hits().hits();
        for (Hit<RestaurantSearchDto> hit : hits) {
            RestaurantSearchDto restaurantSearchDto = hit.source();
            log.info("Found product " + restaurantSearchDto.toString() + ", score " + hit.score());
        }
        if (hits.size() > 0) {
            var bestScoreOsmDto = hits.get(0).source();
            return ResponseEntity.ok(RestaurantSearchDto.builder()
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
