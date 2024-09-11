package com.example.eatsorderapplication.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.Redisson;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DriverDetailsDtoTest.TestConfig.class)
public class DriverDetailsDtoTest {

    @Autowired
    private RedissonReactiveClient redissonClient;

    @Autowired
    private ObjectMapper objectMapper;

    private RGeoReactive<DriverDetailsDto> geo;

    @Configuration
    static class TestConfig {
        private String RedisAddress = "redis://localhost:6379";

        @Bean
        public RedissonReactiveClient redissonClient() {
            Config config = new Config();
            config.useSingleServer().setAddress(RedisAddress);
            StringCodec stringCodec = StringCodec.INSTANCE;
            config.setCodec(stringCodec);
            return Redisson.create(config).reactive();
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @BeforeEach
    public void setUp() {
        geo = redissonClient.getGeo("drivers:geo", new TypedJsonJacksonCodec(DriverDetailsDto.class));
    }

    @Test
    public void testSerializationAndDeserializationWithRedis() throws Exception {
        // 테스트용 DriverDetailsDto 객체 생성
        DriverDetailsDto driverDetails = new DriverDetailsDto("test-driver-id", 37.51430920926344, 127.00024128933235);

        // 객체를 Redis에 저장
        Mono<Void> addResult = geo.add(127.00024128933235, 37.51430920926344, driverDetails).then();
        addResult.block(Duration.ofSeconds(10));

        // 저장한 데이터를 Redis에서 검색
        GeoSearchArgs searchArgs = GeoSearchArgs.from(127.00024128933235, 37.5150416261073)
            .radius(4, org.redisson.api.GeoUnit.KILOMETERS)
            .count(1);

        Mono<List<DriverDetailsDto>> searchResult = geo.search(searchArgs)
            .flatMapIterable(results -> results)
            .collectList();

        List<DriverDetailsDto> results = searchResult.block(Duration.ofSeconds(10));

        // 결과값이 null이 아니고 비어있지 않은지 확인합니다.
        assertNotNull(results);
        assertEquals(1, results.size());

        // 첫 번째 결과값을 DriverDetailsDto로 역직렬화합니다.
        DriverDetailsDto driverDetailsFromRedis = results.get(0);

        // DriverDetailsDto가 null이 아니고 필드가 제대로 채워졌는지 확인합니다.
        assertNotNull(driverDetailsFromRedis);
        assertEquals(driverDetails.getDriverId(), driverDetailsFromRedis.getDriverId());
        assertEquals(driverDetails.getLat(), driverDetailsFromRedis.getLat());
        assertEquals(driverDetails.getLon(), driverDetailsFromRedis.getLon());

        System.out.println(driverDetailsFromRedis);  // 디버깅을 위한 출력
    }


}
