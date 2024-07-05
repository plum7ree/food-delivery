//ref: https://github.com/james-willett/advanced-gatling-web-apps-java/tree/main
package com.example.couponapp.gatling;


import com.example.commonutil.DockerComposeStarter;
import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

//mvn gatling:test -Dgatling.simulationClass=CouponControllerSimulation

public class CouponControllerSimulationTest {
    private static final String DOCKER_COMPOSE_FILE_PATH = ClassLoader.getSystemResource("docker-compose-test.yml").getPath();

    private static DockerComposeStarter dockerComposeStarter;


    static {
        dockerComposeStarter = new DockerComposeStarter(DOCKER_COMPOSE_FILE_PATH);

        // Start PostgreSQL
        try {
            dockerComposeStarter.startServiceAndWaitForLog("coupon-db", ".*ready to accept connections.*", 5, TimeUnit.MINUTES);
            // Start Redis
            dockerComposeStarter.startServiceAndWaitForLog("coupon-redis", ".*Ready to accept connections.*", 5, TimeUnit.MINUTES);

            dockerComposeStarter.startServiceAndWaitForLog("zookeeper", ".*started.*", 5, TimeUnit.MINUTES);

            // Start Kafka
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-1", ".*started.*", 5, TimeUnit.MINUTES);
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-2", ".*started.*", 5, TimeUnit.MINUTES);
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-3", ".*started.*", 5, TimeUnit.MINUTES);

            dockerComposeStarter.startServiceAndWaitForLog("schema-registry", ".*Cluster ID.*", 5, TimeUnit.MINUTES);

            Thread.sleep(5000); // 충분히 켜지길 기다려야함... TODO 시그널 방식으로 어떻게 바꿀까?
            dockerComposeStarter.startServiceAndWaitForLog("coupon-app", ".*Started CouponApp.*", 5, TimeUnit.MINUTES);

            dockerComposeStarter.startServiceAndWaitForLog("coupon-service", ".*Started CouponService.*", 5, TimeUnit.MINUTES);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (dockerComposeStarter != null) {
            dockerComposeStarter.stopAllServices();
        }
    }

    @Test
    public void runSimulation() {
        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
            .simulationClass(CouponControllerSimulation.class.getCanonicalName())
            .resultsDirectory("target/gatling-results")
            .resourcesDirectory("src/test/resources");

        Gatling.fromMap(props.build());
    }

    public static class CouponControllerSimulation extends Simulation {
        private static final int NUM_REQUESTS = 510;
        private static ConcurrentLinkedQueue<Map<String, Object>> couponIssueFeeder = new ConcurrentLinkedQueue<>(IntStream.range(0, NUM_REQUESTS)
            .mapToObj(i -> {
                Map<String, Object> record = new HashMap<>();
                record.put("couponId", "1000000");
                record.put("userId", "123e4567-e89b-12d3-a456-" + String.format("%012d", i));
                return record;
            })
            .collect(Collectors.toList()));
        private static Iterator<Map<String, Object>> couponIssueFeederIterator = couponIssueFeeder.iterator();

        HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8092")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

        ScenarioBuilder scn = scenario("Coupon Issue Test")
            .feed(couponIssueFeederIterator)
            .exec(http("Issue Coupon Request")
                .post("/api/issue")
                .body(StringBody("{ \"couponId\": \"${couponId}\", \"userId\": \"${userId}\" }"))
                .asJson()
                .check(status().is(200))
                .check(jsonPath("$.status").is("SUCCESSFUL"))
                .check(jsonPath("$.message").is("Coupon issued successfully"))
            )
            .pause(1);

        {
            setUp(
                scn.injectOpen( // 사용자 부하를 생성
                    rampUsers(510).during(10)
                )
            ).protocols(httpProtocol);
        }
    }
}