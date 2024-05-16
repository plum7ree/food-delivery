package com.example.eatssearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.example.eatssearch.data.dto.RestaurantDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private static final String INDEX_NAME = "restaurants";
    @Autowired
    private final ElasticsearchClient esClient;

    public List<RestaurantDto> search(String text) throws IOException {
        Query query = MatchQuery.of(m -> m
                .field("name")
                .query(text)
                .fuzziness("AUTO")
        )._toQuery();

        SearchResponse<RestaurantDto> response = esClient.search(s -> s
                .index(INDEX_NAME)
                .query(q -> q
                    .bool(b -> b
                        .should(query)
//                        .should(q2 -> q2
//                            .nested(n -> n
//                                .path("menuDtoList")
//                                .query(nq -> nq
//                                    .match(m -> m
//                                        .field("menuDtoList.name")
//                                        .query(text)
//                                        .fuzziness("AUTO")
//                                    )
//                                )
//                            )
//                        )
                    )
                ),
                RestaurantDto.class
        );

        List<Hit<RestaurantDto>> hits = response.hits().hits();
        List<RestaurantDto> searchResult = hits.stream()
                .map(hit -> {
                    String restaurantId = hit.id();
                    String restaurantName = hit.source().getName().toString();
                    return RestaurantDto.builder().id(UUID.fromString(restaurantId)).name(restaurantName).build();
                })
                .collect(Collectors.toList());

        return searchResult;
    }


}
