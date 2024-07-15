
<h2 style="text-align: center;">Food Delivery Service </h2>

### Tech Stack

- - -
<p>
  <img src="https://img.shields.io/badge/react-blue?logo=react&logoColor=f5f5f5"/>&nbsp
  <img src="https://img.shields.io/badge/spring-green?logo=springboot&logoColor=f5f5f5"/>&nbsp
  <img src="https://img.shields.io/badge/-PostgreSQL-blue?logo=postgresql&logoColor=f5f5f5"/>&nbsp
  <img src="https://img.shields.io/badge/-AWS-orange"/>&nbsp
  <img src="https://img.shields.io/badge/-redis-red?logo=redis&logoColor=f5f5f5"/>&nbsp
  <img src="https://img.shields.io/badge/-kubernetes-blue?logo=kubernetes&logoColor=f5f5f5"/>&nbsp
  <img src="https://img.shields.io/badge/-elasticsearch-green?logo=elasticsearch&logoColor=f5f5f5"/>&nbsp
</p>

### Sample Images

- - -
<table>
    <tr>
      <td style="text-align: center;"><b>Category</b></td>
      <td style="text-align: center;"><b>Search</b></td>
      <td style="text-align: center;"><b>Checkout</b></td>
    </tr>
    <tr>
        <td style="text-align: center;">    
          <img src="readme/category2checkout.gif" alt="Category and Checkout" style="width:250px; height:350px" >
        </td>
        <td style="text-align: center;">    
          <img src="readme/search.gif" alt="Restaurant Search" style="width:250px; height:350px">
        </td>
        <td style="text-align: center;">  
          <img src="readme/checkoutconfirm.gif" alt="Checkout Confirm" style="width:250px; height:350px">
        </td>
    </tr>
</table>
<table>
    <tr>
      <td style="text-align: center;"><b>My Page</b></td>
      <td style="text-align: center;"><b>Restaurant Registration</b></td>
      <td style="text-align: center;"><b></b></td>
    </tr>
    <tr>
        <td style="text-align: center;">    
          <img src="readme/my_page.png" alt="Checkout Confirm" style="width:250px; height:350px">
        </td>
        <td style="text-align: center;">    
            <img src="readme/restaurant_registration.png" alt="Checkout Confirm" style="width:250px; height:350px">
        </td>
        <td style="text-align: center;">    
        </td>
    </tr>
</table>




<p><br></p>  

### Frontend

- - -

- 상태관리 redux

### Backend

- - -    

음식 검색

- elastic search

data 수집

- selenium web crawling

쿠폰 발행
- webflux
  - [CouponController.java](backend/java/backend/coupon/coupon-app/src/main/java/com/example/couponapp/controller/CouponController.java)
  - [VerficationService.java](backend/java/backend/coupon/coupon-app/src/main/java/com/example/couponapp/service/VerificationService.java)
- reliable kafka, idempotence producer(`acks=1`, `enable-idempotence: true`)
  - [application.yml](backend/java/backend/coupon/coupon-app/src/main/resources/application.yml)
- rollback consumer (`enable-auto-commit: false`, manual kafka consumer's `commitSync`)
  - [application.yml](backend/java/backend/coupon/coupon-service/src/main/resources/application.yml)
  - [CouponIssueRequestKafkaConsumer](backend/java/backend/coupon/coupon-service/src/main/java/com/example/couponservice/kafka/listener/CouponIssueRequestKafkaConsumer.java)
- redis 분산락 (`RLockReactive`)
  - [VerficationService.java](backend/java/backend/coupon/coupon-app/src/main/java/com/example/couponapp/service/VerificationService.java)


결제 시스템

- outbox pattern, debezium
  - [debezium docker](docker/kafka/postgres_debezium.yml)
  - [debezium connector](docker/kafka/start-kafka-cluster.sh)
- kafka

### Test
- BDD
- mocking (service, controller layer)
  - example: [VerificationServiceTest.java](/backend/java/backend/coupon/coupon-app/src/test/java/com/example/couponapp/service/VerificationServiceTest.java)
- test container
  - docker compose util: [DockerComposeStarter.java](backend/java/backend/common-util/src/main/java/com/example/commonutil/DockerComposeStarter.java)
    - customized docker compose runner
    - run like:
      - ```yaml
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-1", ".*started.*", 5, TimeUnit.MINUTES);
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-2", ".*started.*", 5, TimeUnit.MINUTES);
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-3", ".*started.*", 5, TimeUnit.MINUTES);
          ```
    - example: [CouponControllerSimulationTest.java](backend/java/backend/coupon/coupon-app/src/test/java/com/example/couponapp/gatling/CouponControllerSimulationTest.java)

### Infra

- oauth2, resource server
- gateway filtering
- config server
- Eureka service discovery
- Kubernetes, helm chart 로 마이크로 서비스 운영 및 배포

<p><br></p>   


### kube node environment setting

we need docker desktop and enable kubernetes

### build & run order

```shell
# all micorservices and obersavation apps
# docker image build with Google Jib added in pom.xml
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>3.3.2</version>
    <configuration>
        <to>
            <image>lomojiki/uber-msa/${project.artifactId}:latest</image>
        </to>
    </configuration>
</plugin>

# for each microservices,
intellij tab maven -> select service -> Plugins -> jib -> jib:dockerBuild

# image name must be lomojiki/uber-msa/driver
# Pushing into hub fails if : lomojiki/uber-msa/driver
push images into hub with a docker desktop

# in values.yaml in each micro service
# default is IfNotPresent which will search on local image first and then remote.
# IfNotPresent vs Always
# https://stackoverflow.com/questions/74006353/difference-between-always-and-ifnotpresent-imagepullpolicy
image:
  repository: uber-msa/driver
  pullPolicy: IfNotPresent
  tag: ""
  
  
# helm build dependencies & install
cd environments
helm dependencies build prod-env
helm install <cluster-name> prod-env/ 
helm uninstall <cluster-name>

cd environment
helm install uber-msa /prod-env
```

<br/>

### Start order in local IDE environment for dev

1. configserver
2. eureka
3. micro services
4. gateway server
   <br/>

### Helm

<b> container ports </b>   

create new chart

```shell
helm create <chart-name>
```

gateway: 8080
eurekaserver: 8761
configserver: 8071   
eatsorderservice: 8074
route: 8075
user: 8077
driver-service: 8078
eatssearch: 8079
schema-registry: 8081
route(python): 8082
login: 8083
driver: 8090
monitoring: 8091
coupon-app: 8092
coupon-service: 8093
# port must be same.

# helm/.../user/values.yml

containerPort: 8077

service:
type: ClusterIP
port: 8077
targetPort: 8077
---

### Kafka Module Architecture

<b>Dependencies</b>

- KafkaAdminClient, KafkaProducerConfig(KafkaTemplate) <- KafkaProducer/KafkaConsumer <- Apps
    - KafkaAdminClient contains `createTopic()`
    - KafkaProducer contains `onAppCreated(ApplicationStartedEvent)` EventListener, which microservice application's
      spring context will fire on their app started.
- KafkaConfigData <- Apps
    - configserver will feed config data into KafkaConfigData object.
    - because of `@ConfigurationProperties(prefix = "appname")`

---

### Note

Module   
`@ComponentScan(basePackages={"com.example"})` necessary to use shared classes.

```
// main pom.xml
// add new modules
    <modules>
        <module>kafka</module>
        <module>kafka/kafka-model</module>
        <module>kafka/kafka-admin</module>
        <module>kafka/kafka-config-data</module>
        ...
    </modules>
// sub modules kafka/pom.xml
    <parent>
        <artifactId>uber-msa</artifactId>
        <groupId>com.example</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <artifactId>kafka</artifactId>
    
// sub-sub modules... kafka/kafka-model/.../pom.xml
    <parent>
        <artifactId>uber-msa</artifactId>
        <groupId>com.example</groupId>
        <version>0.0.1-SNAPSHOT</version>
        <relativePath>../../pom.xml</relativePath>
    </parent>
    <artifactId>kafka-model</artifactId>

```

Domain Driven Design

- Messaging To Dto
    - CallDataMapper

### Micro Service Setting

1. application apps

- pom.xml
    - spring cloud config client
    - eureka client
    - open feign
- application.yml
    - import config server url
    - eureka server url
    - actuator setting
- configserver/app-name.yml setting
- @ComponentScan in main() **Application.java
    - must include itself. no Error even though not included.
    - ``` 
  // com.example.monitoring
  @ComponentScan({"com.example.common.data",
  "com.example.common.config",
  "com.example.kafka.admin",
  "com.example.kafka.config.data",
  "com.example.kafkaconsumer",
  "com.example.monitoring"} )
    ```

2. gateway server

- route rules


