package com.example.eatssearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.example.eatssearch.data.dto.MenuDto;
import com.example.eatssearch.data.dto.RestaurantDto;
import com.example.eatssearch.data.dto.RestaurantTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.time.LocalTime;
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
                    String restaurantName = hit.source().getName();
                    String userId = hit.source().getUserId();
                    RestaurantTypeEnum type = hit.source().getType();
                    LocalTime openTime = hit.source().getOpenTime();
                    LocalTime closeTime = hit.source().getCloseTime();
                    Float rating = hit.source().getRating();
                    String pictureUrl1 = hit.source().getPictureUrl1();
                    List<MenuDto> menuDtoList = hit.source().getMenuDtoList();
                    return RestaurantDto.builder().id(UUID.fromString(restaurantId))
                            .name(restaurantName)
                            .userId(userId)
                            .type(type)
                            .openTime(openTime)
                            .closeTime(closeTime)
                            .rating(rating)
                            .pictureUrl1(pictureUrl1)
                            .menuDtoList(menuDtoList)
                            .build();
                })
                .collect(Collectors.toList());

        return searchResult;
    }


}
