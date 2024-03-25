
### Overview Architecture
<b>Driver Location Streaming</b>   
   
<img src="readme/driver-arch.png" alt="drawing" width="600"/>


### Payment SAGA pattern   
<img src="readme/call-service-saga.png" alt="drawing" width="600"/>   


### Simulation Test   
- Multiple number of drivers
- code: [DriverSimulatorTest.java](driver/src/test/java/com/example/driver/DriverSimulatorTest.java)   



### Monitoring View
![Monitoring Drivers Web](readme/driver_simulation.gif)
- [simple websocket frontend](monitoring/src/main/resources/templates/index.html)   
- currently STOMP based
- Todo: RabbitMQ



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

configserver: 8071   
locationredis: 8072
driver: 8090
monitoring: 8091




### Kafka Module Architecture
<b>Dependencies</b>    
- KafkaAdminClient, KafkaProducerConfig(KafkaTemplate) <- KafkaProducer/KafkaConsumer <- Apps   
  - KafkaAdminClient contains `createTopic()`
  - KafkaProducer contains `onAppCreated(ApplicationStartedEvent)` EventListener, which microservice application's spring context will fire on their app started.
- KafkaConfigData <- Apps
  - configserver will feed config data into KafkaConfigData object.
  - because of `@ConfigurationProperties(prefix = "appname")`


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



