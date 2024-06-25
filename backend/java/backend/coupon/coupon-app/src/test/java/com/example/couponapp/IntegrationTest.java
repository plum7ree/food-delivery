package com.example.couponapp;

import com.example.couponapp.utils.DockerComposeStarter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class IntegrationTest {
    private static final String DOCKER_COMPOSE_FILE_PATH = ClassLoader.getSystemResource("docker-compose-test.yml").getPath();

    private static DockerComposeStarter dockerComposeStarter;

    @BeforeAll
    public static void setup() throws Exception {
        dockerComposeStarter = new DockerComposeStarter(DOCKER_COMPOSE_FILE_PATH);

        // Start PostgreSQL
        dockerComposeStarter.startServiceAndWaitForLog("coupon-db", ".*ready to accept connections.*", 5, TimeUnit.MINUTES);

        // Start Redis
        dockerComposeStarter.startServiceAndWaitForLog("coupon-redis", ".*Ready to accept connections.*", 5, TimeUnit.MINUTES);

        dockerComposeStarter.startServiceAndWaitForLog("zookeeper", ".*started.*", 5, TimeUnit.MINUTES);

        // Start Kafka
        dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-1", ".*started.*", 5, TimeUnit.MINUTES);

        dockerComposeStarter.startServiceAndWaitForLog("schema-registry", ".*started.*", 5, TimeUnit.MINUTES);
//
//        // Start your application services
//        dockerComposeStarter.startServiceAndWaitForLog("coupon-service", "Started CouponServiceApplication", 5, TimeUnit.MINUTES);
//        dockerComposeStarter.startServiceAndWaitForLog("coupon-app", "Started CouponAppApplication", 5, TimeUnit.MINUTES);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (dockerComposeStarter != null) {
            dockerComposeStarter.stopAllServices();
        }
    }

    @Test
    public void testSomething() throws InterruptedException {
        // Your test code here
        Thread.sleep(10000000);
    }
}