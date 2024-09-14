package com.example.websocketserver.application.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis-server}")
    private String RedisAddress;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(RedisAddress);
        StringCodec stringCodec = StringCodec.INSTANCE;
        config.setCodec(stringCodec);
        return Redisson.create(config);
    }

    @Bean
    public RedissonReactiveClient redissonReactiveClient() {
        return redissonClient().reactive();
    }

    @Bean
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

}