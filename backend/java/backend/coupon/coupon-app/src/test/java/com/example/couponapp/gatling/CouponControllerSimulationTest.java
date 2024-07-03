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

import java.util.concurrent.TimeUnit;

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
//        try {
//            Thread.sleep(100000000);
//
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
            .simulationClass(CouponControllerSimulation.class.getCanonicalName())
            .resultsDirectory("target/gatling-results")
            .resourcesDirectory("src/test/resources");

        Gatling.fromMap(props.build());
    }

    public static class CouponControllerSimulation extends Simulation {

        HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8092")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

        ScenarioBuilder scn = scenario("Coupon Issue Test")
            .feed(csv("gatling/coupon_data.csv").random())
            .exec(http("Issue Coupon Request")
                .post("/api/issue")
                .body(StringBody("{ \"couponId\": \"${couponId}\", \"userId\": \"${userId}\" }"))
                .asJson()
                .check(status().is(200))
                .check(jsonPath("$.status").is("SUCCESSFUL"))
                .check(jsonPath("$.message").is("Coupon issued successfully"))
            )
            .pause(1);

        ScenarioBuilder invalidScn = scenario("Invalid Coupon Issue Test")
            .exec(http("Invalid Coupon Issue Request")
                .post("/api/issue")
                .body(StringBody("{ \"couponId\": \"invalid-coupon\", \"userId\": \"user123\" }"))
                .asJson()
                .check(status().is(400))
                .check(jsonPath("$.status").is("FAILED"))
            )
            .pause(1);

        {
            setUp(
                scn.injectOpen(
                    rampUsers(100).during(30),
                    constantUsersPerSec(10).during(30)
                ),
                invalidScn.injectOpen(
                    rampUsers(50).during(30)
                )
            ).protocols(httpProtocol);
        }
    }
}