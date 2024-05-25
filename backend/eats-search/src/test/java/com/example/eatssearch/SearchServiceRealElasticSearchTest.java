package com.example.eatssearch;

import com.example.eatssearch.config.ElasticSearchConfig;
import com.example.eatssearch.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

import java.io.IOException;

@SpringBootTest
@TestPropertySource(properties = {
    "elasticsearch.hostname=localhost",
    "elasticsearch.port=9200"
})
@Slf4j
class SearchServiceRealElasticSearchTest {

    @Autowired
    private SearchService searchService;

    @Test
    public void testSearch() throws IOException {
        var list = searchService.search("burger");
        list.stream().forEach(restaurantDto -> {
            log.info(restaurantDto.toString());
        });
    }
}

//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {ElasticSearchConfig.class}) // or @Import?
////@TestPropertySource(locations = "classpath:application.yml")
//@TestPropertySource(properties = {"eureka.client.enabled=false"})
//@TestPropertySource(properties = {
//    "elasticsearch.hostname=localhost",
//    "elasticsearch.port=5600"
//})
//public class SearchServiceRealElasticSearchTest {
//    @Configuration
//    static class TestConfig {
//        @Bean
//        public SearchService searchService(ElasticsearchClient elasticsearchClient) {
//            return new SearchService(elasticsearchClient);
//        }
//    }
//    @Autowired
//    private SearchService searchService;
//
//    @Test
//    public void testSearch() throws IOException {
//        var list = searchService.search("burger");
//    }
//
//}
