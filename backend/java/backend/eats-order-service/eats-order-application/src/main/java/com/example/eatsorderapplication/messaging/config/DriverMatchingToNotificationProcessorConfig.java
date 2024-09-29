package com.example.eatsorderapplication.messaging.config;

import com.example.commondata.dto.order.UserOrderAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import com.example.eatsorderapplication.application.service.driver.Matching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple3;

import java.time.Duration;
import java.util.Set;

@Configuration
public class DriverMatchingToNotificationProcessorConfig {
    @Bean(name = "maxWindowCount")
    public Integer count() {
        return 5;
    }

    @Bean(name = "driverWindowMaxInterval")
    public Duration interval() {
        return Duration.ofSeconds(10);
    }

    @Bean(name = "nearbySink")
    public Sinks.Many<Tuple3<Set<DriverDetailsDto>, Set<UserOrderAddressDto>, Set<UserOrderAddressDto>>> getNearByDriversSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

    @Bean(name = "matchedResult")
    public Sinks.Many<Matching> getMatchedResultSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
